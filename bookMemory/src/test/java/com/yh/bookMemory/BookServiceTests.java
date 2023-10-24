package com.yh.bookMemory;

import com.yh.bookMemory.dto.BookInfoDTO;
import com.yh.bookMemory.entity.BookInfo;
import com.yh.bookMemory.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BookServiceTests {

    @Autowired
    BookService bookService;

    @Test
    public void testCreate() {
        BookInfoDTO bookInfoDTO = BookInfoDTO.builder()
                .title("sample title")
                .author("sample author")
                .build();
        System.out.println(bookService.createBook(bookInfoDTO));
    }
}
