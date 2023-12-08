package com.yh.bookMemory.service;

import com.yh.bookMemory.dto.BookSentencesDTO;
import com.yh.bookMemory.entity.BookSentences;
import com.yh.bookMemory.repository.BookSentencesRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class EmailServiceImpl implements EmailService, CommonService {

    @Autowired
    BookSentencesRepository bookSentencesRepository;

    @Override
    public List<BookSentencesDTO> pickRandomSentences(int limit) {
        log.info("limit...................."+limit);
        log.info("랜덤 문장 선정.....................................");
        List<BookSentences> sentenceList = bookSentencesRepository.pickRandomSentences(limit);
        List<BookSentencesDTO> sentenceListDto = new ArrayList<>();

        for(BookSentences sentence : sentenceList){
            BookSentencesDTO dto = sentenceEntityToDto(sentence);

            sentenceListDto.add(dto);
        }

        log.info("sentenceListDto......................."+sentenceListDto);

        return sentenceListDto;
//        List<BookSentences> bookSentenceList = bookSentencesRepository.findByBookInfoBookId(bookId);
//
//        //TODO: 각 문장 앞에 별(favorite_flag) 추가하고 db업데이트 로직 구성
//        List<BookSentences> sentenceList = bookSentenceList.stream()
//                .map(bookSentence -> new BookSentences(bookSentence.getSentenceText().replace("<br>", "\r\n")))
//                .collect(Collectors.toList());
//
//        List<BookSentencesDTO> sentenceListDto = new ArrayList<>();
//
//        for(BookSentences sentence : sentenceList){
//            BookSentencesDTO dto = sentenceEntityToDto(sentence);
//
//            sentenceListDto.add(dto);
//        }
//
//        return sentenceListDto;
    }
}
