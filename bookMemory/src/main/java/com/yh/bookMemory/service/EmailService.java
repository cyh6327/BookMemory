package com.yh.bookMemory.service;

import com.yh.bookMemory.dto.BookSentencesDTO;

import java.util.List;

public interface EmailService {

    List<BookSentencesDTO> pickRandomSentences(String sentenceSortKey, long sendCnt, int limit) throws Exception;

    boolean sendMail(String email, String sentenceSortKey, int limit);
}
