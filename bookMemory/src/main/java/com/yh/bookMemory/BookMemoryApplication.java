package com.yh.bookMemory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
@SpringBootApplication이 실행되면 아래의 패키지 경로의 컨텍스트들이 빈으로 등록된다
	- 해당 어노테이션이 붙은 클래스 패키지
	- @ComponentScan 어노테이션이 붙은 클래스 패키지
따라서 test 폴더에 있는 클래스는 스캔하지 않는 것
*/
@SpringBootApplication
public class BookMemoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookMemoryApplication.class, args);
	}

}
