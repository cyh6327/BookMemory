package com.yh.bookMemory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SentenceLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sentenceLogId;

    @Column
    private Date sendDate;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private BookSentences bookSentences;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Users users;
}
