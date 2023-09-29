package com.yh.bookMemory.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.time.LocalDateTime;

@Data   //Getter/Setter, toString(), equals(), hashCode() 자동 생성
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
}
