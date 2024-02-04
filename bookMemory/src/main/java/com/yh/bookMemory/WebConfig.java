package com.yh.bookMemory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                //.allowedOrigins("http://localhost:9000") //클라이언트 호스트
                //.allowedOrigins("*")
                .allowedOriginPatterns("*")
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
        // registry.addInterceptor(new LoggerInterceptor())
        // new()를 통해 Interceptor 객체를 만들어서 등록하면 Spring Container에서 이 Interceptor를 관리하지 못하기 때문에
        // @Autowired 로 서비스에 의존성 주입이 불가능했던 것
        // ==> @Bean 으로 Interceptor 객체를 생성해 스프링에서 관리하게 변경
        registry.addInterceptor(loggerInterceptor())
                .excludePathPatterns("/css/**", "/images/**", "/js/**", "/login");    //특정 url 패턴은 예외
    }

    @Bean
    public LoggerInterceptor loggerInterceptor() {
        return new LoggerInterceptor();
    }
}
