package com.yh.bookMemory.repository;

import com.yh.bookMemory.entity.BookSentences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookSentencesRepository extends JpaRepository<BookSentences, Long>{
    @Query("SELECT bs FROM BookSentences bs WHERE bs.bookInfo.bookId = :bookId")
    Optional<BookSentences> findByBookInfo_BookId(Long bookId);
}