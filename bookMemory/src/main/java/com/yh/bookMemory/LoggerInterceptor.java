package com.yh.bookMemory;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.yh.bookMemory.jwt.JwtProperties;
import com.yh.bookMemory.jwt.JwtTokenVerifier;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Enumeration;
import java.util.Objects;

public class LoggerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("==================== START ====================");
        System.out.println(" Request URI \t: " + request.getRequestURI() + request.getMethod());

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

            if(jwtTokenVerifier.getAccessTokenInfo(receivedToken).getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                response.sendRedirect(request.getContextPath()+"/book");
                return false;
            }

            verifiedJwt = Objects.requireNonNull(jwtTokenVerifier.getAccessTokenInfo(receivedToken).getBody()).toString();

            System.out.println("verifiedJwt.........................."+verifiedJwt);
        } else {
            return true; //TODO: option으로 날릴때 else로 들어오지만 테스트릍 위해 일단 true로 리턴해줬지만 변경해줘야함
        }

        return verifiedJwt != null;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("==================== END ====================");
    }

}
