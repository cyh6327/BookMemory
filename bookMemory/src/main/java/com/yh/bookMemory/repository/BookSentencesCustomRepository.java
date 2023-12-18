package com.yh.bookMemory.repository;

import com.yh.bookMemory.entity.BookSentences;

import java.util.List;

public interface BookSentencesCustomRepository {

    List<BookSentences> findByBookInfoBookId(Long bookId);

    List<BookSentences> pickRandomSentences(double sortKey, long sendCnt, int limit);
}
