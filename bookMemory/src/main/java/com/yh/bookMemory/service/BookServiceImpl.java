package com.yh.bookMemory.service;

import com.yh.bookMemory.dto.BookInfoDTO;
import com.yh.bookMemory.dto.BookSentencesDTO;
import com.yh.bookMemory.dto.PageRequestDTO;
import com.yh.bookMemory.dto.PageResultDTO;
import com.yh.bookMemory.entity.BookInfo;
import com.yh.bookMemory.entity.BookSentences;
import com.yh.bookMemory.repository.BookInfoRepository;
import com.yh.bookMemory.repository.BookSentencesRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

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
    public PageResultDTO<BookInfoDTO, BookInfo> getAllBookList(PageRequestDTO requestDTO) {
        log.info("getAllBookList....................");
        Pageable pageable = requestDTO.getPageable(Sort.by("bookId").descending());
        Page<BookInfo> result = bookInfoRepository.findAll(pageable);
        Function<BookInfo, BookInfoDTO> fn = (entity -> entityToDto(entity));

        return new PageResultDTO<>(result,fn);
    }

    @Override
    public BookSentencesDTO read(Long bookId) {
       //Optional<BookSentences> result = bookSentencesRepository.findById(bookId);
       Optional<BookSentences> result = bookSentencesRepository.findByBookInfo_BookId(bookId);
        return result.isPresent()? sentenceEntityToDto(result.get()) : null;
    }
}
