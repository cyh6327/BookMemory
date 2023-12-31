package com.yh.bookMemory.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BookSentences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookSentenceId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String sentenceText;

    @Column(length = 1)
    private Character favoriteFlag;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private BookInfo bookInfo;

    public BookSentences(String sentenceText) {
        this.sentenceText = sentenceText;
    }
}
