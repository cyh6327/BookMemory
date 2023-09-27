package com.yh.bookMemory.dto;

import com.yh.bookMemory.entity.BookInfo;
import lombok.Builder;
import lombok.Data;

@Data   //Getter/Setter, toString(), equals(), hashCode() 자동 생성
@Builder(toBuilder = true)
public class BookSentencesDTO {
    private Long bookSentenceId;
    private String sentenceText;
    private Character favoriteFlag;
    private BookInfo bookInfo;
}
