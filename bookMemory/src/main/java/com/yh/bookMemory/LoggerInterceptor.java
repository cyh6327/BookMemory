package com.yh.bookMemory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Enumeration;

public class LoggerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("==================== START ====================");
        System.out.println(" Request URI \t: " + request.getRequestURI());

        String receivedToken = null;
        String type = "Bearer";
        Enumeration<String> headers = request.getHeaders("Authorization");
        System.out.println("headers.........................."+headers.toString());
        while (headers.hasMoreElements()) {
            String value = headers.nextElement();
            if (value.toLowerCase().startsWith(type.toLowerCase())) {
                receivedToken = value.substring(type.length()).trim();
            }
        }
        System.out.println("accessToken from cookie.........................."+receivedToken);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("==================== END ====================");
    }

}
