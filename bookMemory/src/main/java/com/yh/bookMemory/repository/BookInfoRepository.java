package com.yh.bookMemory.repository;

import com.yh.bookMemory.entity.BookInfo;
import com.yh.bookMemory.entity.BookSentences;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookInfoRepository extends JpaRepository<BookInfo, Long> {
    List<BookInfo> getReferenceByUsersUserKey(Long userKey);
    BookInfo getReferenceByBookId(Long bookId);
}
