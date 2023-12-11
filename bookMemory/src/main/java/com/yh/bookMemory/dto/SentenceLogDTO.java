package com.yh.bookMemory.dto;

import com.yh.bookMemory.entity.BookInfo;
import com.yh.bookMemory.entity.BookSentences;
import com.yh.bookMemory.entity.SentenceLog;
import com.yh.bookMemory.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SentenceLogDTO {
    private Long sentenceLogId;

    private Date sendDate;

    private BookSentences bookSentences;

    private Users users;

    public SentenceLog toEntity() {
        return SentenceLog.builder()
                .sentenceLogId(sentenceLogId)
                .sendDate(sendDate)
                .bookSentences(bookSentences)
                .users(users)
                .build();
    }
}
