package com.yh.bookMemory.service;

import com.yh.bookMemory.dto.BookInfoDTO;
import com.yh.bookMemory.dto.BookSentencesDTO;
import com.yh.bookMemory.entity.BookInfo;
import com.yh.bookMemory.entity.BookSentences;
import com.yh.bookMemory.repository.BookInfoRepository;
import com.yh.bookMemory.repository.BookSentencesRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.util.Optional;

@Service
@Log4j2
public class BookServiceImpl implements BookService {

    @Autowired
    BookInfoRepository bookInfoRepository;

    @Autowired
    BookSentencesRepository bookSentencesRepository;

    @Override
    public Long create(BookInfoDTO dto) {
        log.info("DTO....................");
        log.info(dto);

        BookInfo entity = dtoToEntitiy(dto);

        log.info("entity....................");
        log.info(entity);

        bookInfoRepository.save(entity);

        return entity.getBookId();
    }

    @Override
    public BookSentencesDTO read(Long bookId) {
        Optional<BookSentences> result = bookSentencesRepository.findById(bookId);
        return null;
    }
}
