package com.yh.bookMemory.repository;

import com.yh.bookMemory.entity.BookInfo;
import com.yh.bookMemory.entity.BookSentences;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookSentencesRepository extends JpaRepository<BookSentences, Long>{
}