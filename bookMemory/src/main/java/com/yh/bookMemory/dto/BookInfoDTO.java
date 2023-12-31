package com.yh.bookMemory.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yh.bookMemory.entity.BookInfo;
import com.yh.bookMemory.entity.Users;
import jakarta.persistence.Column;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.time.LocalDateTime;

// TODO: dto는 setter를 잘 사용하지 않는 것 같던데 좀 더 알아보고 변경하기(어노테이션도 정확히 어떤 역할을 하는지 숙지)
//@Data   //Getter/Setter, toString(), equals(), hashCode() 자동 생성
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BookInfoDTO {

    private Long bookId;
    private String title;
    private String author;
    //@JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date readingStartDate;
//    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date readingEndDate;
    private Double rating;
    private String review;
    private String memo;
    private Character favoriteFlag;
    private String genre;
    private Users users;

    public BookInfo toEntity() {
        return BookInfo.builder()
                .bookId(bookId)
                .title(title)
                .author(author)
                .readingStartDate(readingStartDate)
                .readingEndDate(readingEndDate)
                .rating(rating)
                .review(review)
                .memo(memo)
                .favoriteFlag(favoriteFlag)
                .genre(genre)
                .users(users)
                .build();
    }
}
