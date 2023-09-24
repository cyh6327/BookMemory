package com.yh.bookMemory;

import com.yh.bookMemory.entity.BookInfo;
import com.yh.bookMemory.entity.BookSentences;
import com.yh.bookMemory.repository.BookInfoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class BookInfoRepositoryTests {

    @Autowired
    BookInfoRepository bookInfoRepository;

    @Test
    public void insertBookInfoTest() {
        BookInfo book = BookInfo.builder().title("title").author("author").build();
        bookInfoRepository.save(book);
    }



}
