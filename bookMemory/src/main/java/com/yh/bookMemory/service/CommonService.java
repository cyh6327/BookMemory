package com.yh.bookMemory.service;

import com.yh.bookMemory.dto.BookInfoDTO;
import com.yh.bookMemory.dto.BookSentencesDTO;
import com.yh.bookMemory.dto.UserDTO;
import com.yh.bookMemory.entity.BookInfo;
import com.yh.bookMemory.entity.BookSentences;
import com.yh.bookMemory.entity.Users;
import com.yh.bookMemory.jwt.JwtTokenVerifier;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Objects;

public interface CommonService {
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
                .genre(dto.getGenre())
                .users(dto.getUsers())
                .build();
        return entity;
    }

    default BookInfoDTO entityToDto(BookInfo entity) {
        BookInfoDTO dto = BookInfoDTO.builder()
                .bookId(entity.getBookId())
                .title(entity.getTitle())
                .author(entity.getAuthor())
                .readingStartDate(entity.getReadingStartDate())
                .readingEndDate(entity.getReadingEndDate())
                .rating(entity.getRating())
                .review(entity.getReview())
                .memo(entity.getMemo())
                .favoriteFlag(entity.getFavoriteFlag())
                .genre(entity.getGenre())
                .users(entity.getUsers())
                .build();
        return dto;
    }

    default BookSentencesDTO sentenceEntityToDto(BookSentences entity) {
        BookSentencesDTO dto = BookSentencesDTO.builder()
                .bookSentenceId(entity.getBookSentenceId())
                .sentenceText(entity.getSentenceText())
                .favoriteFlag(entity.getFavoriteFlag())
                .bookInfo(entity.getBookInfo())
                .build();
        return dto;
    }

    default Long getUserKeyFromJwt() {
        Object accessToken = RequestContextHolder.getRequestAttributes().getAttribute("accessToken", RequestAttributes.SCOPE_REQUEST);

        if(accessToken == null) {
            return null;
        }

        JwtTokenVerifier jwtTokenVerifier = new JwtTokenVerifier(accessToken.toString());
        Long userKey = Long.parseLong(jwtTokenVerifier.getJwtInfo(accessToken.toString(), "user_key"));

        return userKey;
    }
}
