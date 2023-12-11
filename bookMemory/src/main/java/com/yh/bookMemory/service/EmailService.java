package com.yh.bookMemory.service;

import com.yh.bookMemory.dto.BookSentencesDTO;

import java.util.List;

public interface EmailService {

    List<BookSentencesDTO> pickRandomSentences(int limit);

    boolean sendMail(String email);
}
