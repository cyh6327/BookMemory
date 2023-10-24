package com.yh.bookMemory.service;

import com.yh.bookMemory.dto.BookInfoDTO;
import com.yh.bookMemory.dto.BookSentencesDTO;
import com.yh.bookMemory.dto.PageRequestDTO;
import com.yh.bookMemory.dto.PageResultDTO;
import com.yh.bookMemory.entity.BookInfo;
import com.yh.bookMemory.entity.BookSentences;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

public interface BookService {
    BookInfoDTO createBook(BookInfoDTO dto);

    PageResultDTO<BookInfoDTO, BookInfo> getAllBookList(PageRequestDTO requestDTO);

    List<BookSentencesDTO> insertSentenceFromFile(long bookId, String title) throws IOException;

    BookInfoDTO getBookInfo(long bookId);

    List<BookSentencesDTO> getSentences(long bookId) throws IOException;
}
