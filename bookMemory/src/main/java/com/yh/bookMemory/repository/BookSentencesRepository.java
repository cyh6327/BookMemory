package com.yh.bookMemory.repository;

import com.yh.bookMemory.entity.BookSentences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Book;
import java.util.List;
import java.util.Optional;

public interface BookSentencesRepository extends JpaRepository<BookSentences, Long>{
    List<BookSentences> findByBookInfoBookId(Long bookId);

    //TODO: postgresql nativeQuery 사용시 order by 난수가 안되고 random() 을 사용해야만 적용됨
    //jpql 로 해당 기능 구현할 수 있는지 알아보기
    @Query(value = "SELECT * FROM (SELECT *, ROW_NUMBER() OVER(ORDER BY :sortKey) AS ROWNUM FROM BOOK_SENTENCES) WHERE ROWNUM > :sendCnt LIMIT :limit", nativeQuery = true)
    List<BookSentences> pickRandomSentences(@Param(value = "sortKey") double sortKey, @Param("sendCnt") long sendCnt, @Param("limit") int limit);

}