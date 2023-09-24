package com.yh.bookMemory;

import com.yh.bookMemory.entity.BookInfo;
import com.yh.bookMemory.entity.BookSentences;
import com.yh.bookMemory.repository.BookInfoRepository;
import com.yh.bookMemory.repository.BookSentencesRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.print.Book;
import java.util.Optional;

@SpringBootTest
public class BookSentencesRepositoryTests {

    @Autowired
    BookSentencesRepository bookSentencesRepository;

    @Test
    public void insertBookSentencesTest() {
        BookInfo bookInfo = BookInfo.builder().title("join test").author("join test").build();

        BookSentences sentences = BookSentences.builder().sentenceText("join sentence text").bookInfo(bookInfo).build();
        bookSentencesRepository.save(sentences);
    }

    @Test
    public void findBookSentencesTest() {
        Optional<BookSentences> result = bookSentencesRepository.findById(1L);
        BookSentences sentences = result.get();

        System.out.println(sentences);
        System.out.println(sentences.getBookInfo());
    }

}
