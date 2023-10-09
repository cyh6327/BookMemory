package com.yh.bookMemory.repository;

import com.yh.bookMemory.entity.BookSentences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Book;
import java.util.List;
import java.util.Optional;

public interface BookSentencesRepository extends JpaRepository<BookSentences, Long>{
    List<BookSentences> findByBookInfoBookId(Long bookId);

//    @Query(value = "SELECT * FROM BOOK_SENTENCES by RAND() limit 8",nativeQuery = true)
//    List<BookSentences> pickRandomSentences(int limit);
}