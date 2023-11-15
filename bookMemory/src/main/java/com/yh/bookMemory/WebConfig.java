package com.yh.bookMemory;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:9000") //클라이언트 호스트
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true) // 쿠키 인증 요청 허용
//                .allowedHeaders("Authorization", "Content-Type")
                .allowedHeaders("*")
                .maxAge(3000);

//		addMapping - CORS를 적용할 url의 패턴을 정의 (/** 로 모든 패턴을 가능하게 함)
//		allowedOrigins - 허용할 origin을 정의 (* 로 모든 origin을 허용, 여러개도 지정가능)
//		allowedMethods - HTTP Method를 지정 (* 로 모든 Method를 허용)
//		maxAge - 원하는 시간만큼 pre-flight 리퀘스트를 캐싱
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggerInterceptor())
                .excludePathPatterns("/css/**", "/images/**", "/js/**", "/login", "/book");    //특정 url 패턴은 예외
    }
}
