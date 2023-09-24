package com.yh.bookMemory.service;

import com.yh.bookMemory.dto.BookInfoDTO;
import com.yh.bookMemory.dto.BookSentencesDTO;
import com.yh.bookMemory.entity.BookInfo;

public interface BookService {

    default BookInfo dtoToEntitiy(BookInfoDTO dto) {
        BookInfo entity = BookInfo.builder()
                .bookId(dto.getBookId())
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .readingStartDate(dto.getReadingStartDate())
                .readingEndDate(dto.getReadingEndDate())
                .rating(dto.getRating())
                .review(dto.getReview())
                .memo(dto.getMemo())
                .favoriteFlag(dto.getFavoriteFlag())
                .build();
        return entity;
    }

    Long create(BookInfoDTO dto);

    BookSentencesDTO read(Long bookId);
}
