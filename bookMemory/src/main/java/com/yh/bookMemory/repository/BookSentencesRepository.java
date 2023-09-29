package com.yh.bookMemory.repository;

import com.yh.bookMemory.entity.BookSentences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookSentencesRepository extends JpaRepository<BookSentences, Long>{
    List<BookSentences> findByBookInfoBookId(Long bookId);
}