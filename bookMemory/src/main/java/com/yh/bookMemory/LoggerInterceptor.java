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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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

        Cookie[] cookies = request.getCookies();
        String accessToken = "";
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                }
            }
        }
        System.out.println("find accessToken......."+accessToken);

//        String receivedToken = null;
//        String type = "Bearer ";
        String verifiedJwt = null;
        //Enumeration<String> headers = request.getHeaders("Authorization");

//        while (headers.hasMoreElements()) {
//            String value = headers.nextElement();
//            System.out.println("header.............."+value);
//            if (value.toLowerCase().startsWith(type.toLowerCase())) {
//                System.out.println("bearer header.............."+value);
//                receivedToken = value.substring(type.length()).trim();
//            }
//        }
//        System.out.println("accessToken from cookie.........................."+receivedToken);

        if(accessToken == null) {
            //response.sendRedirect(request.getContextPath()+"/book");
            // receivedToken이 없어도 메인화면은 호출이 가능하다.
            return request.getRequestURI().equals("/book");
        }

        /*
            getAccessTokenInfo 에서 넘어온 status로 1.유효시간이 경과했는지 아니면 2.아예 유효하지 않은 토큰인지 확인
            2인 경우 에러 발생시키고 1인 경우 refresh token 만료여부 체크 후
            1. 유효하면 refresh token 검증하여 access token 재발급 2. 만료됐다면 refresh token 재발급 후 access token 재발급
         */
        JwtTokenVerifier jwtTokenVerifier = new JwtTokenVerifier(JwtProperties.SECRET);
        try {
            jwtTokenVerifier.verify(accessToken);
        } catch (JWTVerificationException e){
            // 유효기간 만료
            System.out.println("accesstoken has expired..........................");

            // access token에서 user_key 정보를 가져온다.
            Object userKey = jwtTokenVerifier.getJwtInfo(Objects.toString(accessToken), "user_key");
            if(userKey == null) {
                return false;
            }

            // access token을 재발급하기 위해 유저의 refresh token 을 가져온다.
            Users user = userService.getUserInfoByUserKey(Long.parseLong(userKey.toString()));
            String userRefreshToken = user.getRefreshToken();
            if(userRefreshToken == null) {
                return false;
            }
            System.out.println("userRefreshToken.........................."+userRefreshToken);

            // refresh token의 유효기간이 지나지 않았는지 검증한다.
            try {
                jwtTokenVerifier.verify(userRefreshToken);
            } catch(JWTVerificationException e1) {
                System.out.println("userRefreshToken has expired........................");
                // refresh token이 만료되었다면 재발급하여 db에 업데이트한다.
                userService.updateRefreshToken(null, Long.parseLong(Objects.toString(userKey)));
            } finally {
                // 최종적으로 access token을 재발급한다.
                verifiedJwt = CreateJwt.createAccessToken(user);
                System.out.println("updated access token : "+verifiedJwt);

                // access token을 쿠키에 저장한다.
                ResponseCookie cookie = ResponseCookie.from("accessToken", verifiedJwt)
                        .path("/")
                        .maxAge(3600)
                        .build();

                HttpHeaders header = new HttpHeaders();
                header.set("Set-Cookie", cookie.toString());
            }

        } catch (Exception e) {
            // 유효하지 않은 토큰이거나 이외의 예외상황
            return false;
        }

        verifiedJwt = Objects.requireNonNull(jwtTokenVerifier.getJwtTokenInfo(accessToken));
        System.out.println("verifiedJwt.........................."+verifiedJwt);

        RequestContextHolder.currentRequestAttributes().setAttribute("accessToken", verifiedJwt, RequestAttributes.SCOPE_REQUEST);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("==================== END ====================");
    }

}
