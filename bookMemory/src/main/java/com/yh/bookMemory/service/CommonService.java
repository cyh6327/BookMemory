package com.yh.bookMemory.service;

import com.yh.bookMemory.dto.BookInfoDTO;
import com.yh.bookMemory.dto.BookSentencesDTO;
import com.yh.bookMemory.dto.UserDTO;
import com.yh.bookMemory.entity.BookInfo;
import com.yh.bookMemory.entity.BookSentences;
import com.yh.bookMemory.entity.Users;
import com.yh.bookMemory.jwt.JwtProperties;
import com.yh.bookMemory.jwt.JwtTokenVerifier;
import lombok.Value;
import lombok.extern.log4j.Log4j2;
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

//    default Long getUserKeyFromJwt() {
//        System.out.println("getUserKeyFromJwt...................");
//        Object accessToken = RequestContextHolder.getRequestAttributes().getAttribute("accessToken", RequestAttributes.SCOPE_REQUEST);
//        System.out.println("accessToken..................."+accessToken);
//        if(accessToken == null) {
//            throw new NullPointerException("accessToken 값이 존재하지 않습니다.");
//        }
//
//        JwtTokenVerifier jwtTokenVerifier = new JwtTokenVerifier(JwtProperties.SECRET);
//        String userKey = jwtTokenVerifier.getJwtInfo(accessToken.toString(), "user_key");
//        if(userKey == null) {
//            throw new NullPointerException("jwt token에서 user_key 정보를 받아오는데 실패하였습니다.");
//        }
//
//        return Long.parseLong(jwtTokenVerifier.getJwtInfo(accessToken.toString(), "user_key"));
//    }
}
