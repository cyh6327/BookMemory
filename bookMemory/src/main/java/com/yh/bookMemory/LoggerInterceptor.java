package com.yh.bookMemory;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.yh.bookMemory.jwt.JwtProperties;
import com.yh.bookMemory.jwt.JwtTokenVerifier;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
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
            JwtTokenVerifier jwtTokenVerifier = new JwtTokenVerifier(JwtProperties.SECRET);

            // getAccessTokenInfo 에서 넘어온 status로 1.유효시간이 경과했는지 아니면 2.아예 유효하지 않은 토큰인지 확인
            // 2인 경우 에러 발생시키고 1인 경우 refresh token 유효한지 체크 후 유효하면 refresh token 검증하여 access token 재발급
            // 그리고 access token이 유효한 경우에도 바로 넘기는 게 아니라 refresh도 만료 여부를 검증해야 한다.
            if(jwtTokenVerifier.getAccessTokenInfo(receivedToken).getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                //response.sendRedirect(request.getContextPath()+"/book");
            }

//            Map<String,Object> verifiedResult = jwtTokenVerifier.getAccessTokenInfo(receivedToken).getBody();
//            verifiedJwt = verifiedResult.get("verifiedJwt").toString();
            //userKey = Long.parseLong(verifiedResult.get("userKey").toString());
            verifiedJwt = Objects.requireNonNull(jwtTokenVerifier.getAccessTokenInfo(receivedToken).getBody()).toString();

            System.out.println("verifiedJwt.........................."+verifiedJwt);
            //System.out.println("userKey.........................."+userKey);

            RequestContextHolder.currentRequestAttributes().setAttribute("accessToken", verifiedJwt, RequestAttributes.SCOPE_REQUEST);

        } else {
            //response.sendRedirect(request.getContextPath() + "/book");
            return false; //TODO: jwt토큰 인증이 안되었을 때 정상적으로 대시보드로 리다이렉트하는지 테스트 필요
        }

        return verifiedJwt != null;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("==================== END ====================");
    }

}
