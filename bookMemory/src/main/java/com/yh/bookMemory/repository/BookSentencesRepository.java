package com.yh.bookMemory.repository;

import com.yh.bookMemory.entity.BookSentences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookSentencesRepository extends JpaRepository<BookSentences, Long> {
    List<BookSentences> findByBookInfoBookId(Long bookId);
    BookSentences findByBookSentenceId(Long bookSentenceId);
}