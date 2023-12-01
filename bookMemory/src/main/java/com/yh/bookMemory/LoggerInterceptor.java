package com.yh.bookMemory;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.yh.bookMemory.entity.Users;
import com.yh.bookMemory.jwt.CreateJwt;
import com.yh.bookMemory.jwt.JwtProperties;
import com.yh.bookMemory.jwt.JwtTokenVerifier;
import com.yh.bookMemory.repository.UserRefreshTokenMapping;
import com.yh.bookMemory.repository.UserRefreshTokenRepository;
import com.yh.bookMemory.repository.UserRepository;
import com.yh.bookMemory.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;

public class LoggerInterceptor implements HandlerInterceptor {

    @Autowired
    UserService userService;

//    @Autowired
//    private ApplicationContext applicationContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //UserService userService = applicationContext.getBean(UserService.class);

        System.out.println("==================== START ====================");
        System.out.println(" Request URI \t: " + request.getRequestURI() + request.getMethod());

        if(request.getMethod().equals("OPTIONS")) {
            return true;
        }

        String receivedToken = null;
        String type = "Bearer ";
        String verifiedJwt = null;
        Enumeration<String> headers = request.getHeaders("Authorization");

        while (headers.hasMoreElements()) {
            String value = headers.nextElement();
            System.out.println("header.............."+value);
            if (value.toLowerCase().startsWith(type.toLowerCase())) {
                System.out.println("bearer header.............."+value);
                receivedToken = value.substring(type.length()).trim();
            }
        }
        System.out.println("accessToken from cookie.........................."+receivedToken);

        if(receivedToken != null) {
            /*
                getAccessTokenInfo 에서 넘어온 status로 1.유효시간이 경과했는지 아니면 2.아예 유효하지 않은 토큰인지 확인
                2인 경우 에러 발생시키고 1인 경우 refresh token 유효한지 체크 후 유효하면 refresh token 검증하여 access token 재발급
                그리고 access token이 유효한 경우에도 바로 넘기는 게 아니라 refresh도 만료 여부를 검증해야 한다.
             */

            JwtTokenVerifier jwtTokenVerifier = new JwtTokenVerifier(JwtProperties.SECRET);
            Object userKey = jwtTokenVerifier.getJwtInfo(Objects.toString(receivedToken), "user_key");

            if(userKey == null) {
                return false;
            }

            // 유저의 refresh token 가져오기
            Users user = userService.getUserInfoByUserKey(Long.parseLong(userKey.toString()));
            String userRefreshToken = user.getRefreshToken();
            System.out.println("userRefreshToken.........................."+userRefreshToken);

            if(userRefreshToken == null) {
                return false;
            }

            System.out.println("userRefreshToken.........................."+userRefreshToken);

            // access token 유효기간 만료
            if(jwtTokenVerifier.getJwtTokenInfo(receivedToken).getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                System.out.println("accesstoken validity period has expired..........................");
                try {
                    // refresh token도 유효기간이 지났을 경우 해당 유저의 refresh token을 업데이트한다.
                    if(jwtTokenVerifier.getJwtTokenInfo(userRefreshToken).getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                        System.out.println("refresh token validity period has expired..........................");
                        user = userService.updateRefreshToken(null, Long.parseLong(Objects.toString(userKey)));
                        System.out.println("updated refresh token : "+user.getRefreshToken());
                    } else {
                        // refresh token이 유효할 경우에는 해당 refresh token으로 access token을 재발급한다.
                        System.out.println("refresh token is valid..................................");
                        user = userService.getUserInfoByUserKey(Long.parseLong(Objects.toString(userKey)));
                        verifiedJwt = CreateJwt.createAccessToken(user);
                        System.out.println("updated access token : "+verifiedJwt);
                    }
                } catch(JWTVerificationException e) {
                    return false;
                }
            } else if(jwtTokenVerifier.getJwtTokenInfo(receivedToken).getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                // 유효하지 않은 access token
                return false;
            } else {
                // access token이 유효하지만 refresh token이 만료일 경우 업데이트
                if(jwtTokenVerifier.getJwtTokenInfo(userRefreshToken).getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                    System.out.println("refresh token validity period has expired..........................");
                    user = userService.updateRefreshToken(null, Long.parseLong(Objects.toString(userKey)));
                    System.out.println("updated refresh token : "+user.getRefreshToken());
                }
                return true;
            }

//            Map<String,Object> verifiedResult = jwtTokenVerifier.getAccessTokenInfo(receivedToken).getBody();
//            verifiedJwt = verifiedResult.get("verifiedJwt").toString();
            //userKey = Long.parseLong(verifiedResult.get("userKey").toString());
            verifiedJwt = Objects.requireNonNull(jwtTokenVerifier.getJwtTokenInfo(receivedToken).getBody()).toString();

            System.out.println("verifiedJwt.........................."+verifiedJwt);
            //System.out.println("userKey.........................."+userKey);

            RequestContextHolder.currentRequestAttributes().setAttribute("accessToken", verifiedJwt, RequestAttributes.SCOPE_REQUEST);
        } else {
            //response.sendRedirect(request.getContextPath() + "/book");
            if(request.getRequestURI().equals("/book")) {
                return true;
            }
            //response.sendRedirect(request.getContextPath()+"/book");
            return false; //TODO: jwt토큰 인증이 안되었을 때 정상적으로 대시보드로 리다이렉트하는지 테스트 필요
        }


        return verifiedJwt != null;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("==================== END ====================");
    }

}
