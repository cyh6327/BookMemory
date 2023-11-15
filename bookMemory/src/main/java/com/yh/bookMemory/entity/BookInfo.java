package com.yh.bookMemory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BookInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column
    private Date readingStartDate;

    @Column
    private Date readingEndDate;

    @Column
    private Double rating;

    @Column(columnDefinition = "TEXT")
    private String review;

    @Column(columnDefinition = "TEXT")
    private String memo;

    @Builder.Default
    @Column(length = 1)
    private Character favoriteFlag = 'N';

    @Column
    private String genre;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Users users;
}
