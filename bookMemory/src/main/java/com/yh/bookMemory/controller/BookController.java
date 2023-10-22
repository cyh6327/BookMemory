package com.yh.bookMemory.controller;

import com.yh.bookMemory.dto.BookInfoDTO;
import com.yh.bookMemory.dto.BookSentencesDTO;
import com.yh.bookMemory.dto.PageRequestDTO;
import com.yh.bookMemory.entity.BookInfo;
import com.yh.bookMemory.entity.BookSentences;
import com.yh.bookMemory.repository.BookSentencesRepository;
import com.yh.bookMemory.service.BookService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("/book")
@RestController
@Log4j2
public class BookController {

    //TODO: view 로드하는 메서드 제거하고 화면단에서 로드하는 걸로 변경하기
    //TODO: 화면단 fetch api 활용

    @Autowired
    BookService bookService;

    @Autowired
    BookSentencesRepository bookSentencesRepository;

    @PostMapping("/create")
    public ResponseEntity createBook(BookInfoDTO bookInfoDTO) throws IOException {
        log.info("createBook...............");

        BookInfo createdBook = bookService.createBook(bookInfoDTO);

        if(createdBook == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(createdBook);
    }

    @GetMapping("/detail/{bookId}")
    public ModelAndView bookDetailPage(String title, @ModelAttribute("requestDTO") PageRequestDTO requestDTO, @PathVariable Long bookId,
                                       HttpServletResponse response) throws IOException {
        log.info("move bookDetailPage...............");

        BookInfo bookInfo = bookService.getBookInfo(bookId);
        log.info("bookInfo..............."+bookInfo.toString());

        List<BookSentences> bookSentencesList = bookSentencesRepository.findByBookInfoBookId(bookId);

        //TODO: 각 문장 앞에 별(favorite_flag) 추가하고 db업데이트 로직 구성
        List<BookSentences> modifiedSentences = bookSentencesList.stream()
                .map(bookSentence -> new BookSentences(bookSentence.getSentenceText().replace("<br>", "\r\n")))
                .collect(Collectors.toList());

        ModelAndView mav = new ModelAndView();
        mav.setViewName("bookDetail");
        mav.addObject("bookInfo",bookInfo);
        mav.addObject("sentenceList",modifiedSentences);

        return mav;
    }

    @PostMapping(path="/sentence/file/{bookId}/{title}")
    public ResponseEntity insertSentenceFromFile(@PathVariable(value = "bookId") Long bookId, @PathVariable(value = "title") String title,
                                                 HttpServletResponse response) throws IOException {
        List<BookSentencesDTO> sentenceListDto = bookService.insertSentenceFromFile(bookId, title);

        if(sentenceListDto == null || sentenceListDto.size() == 0) {
            return ResponseEntity.notFound().build();
        }

        String redirectUri = "/book/detail/"+bookId;
        response.sendRedirect(redirectUri);

        return ResponseEntity.ok().build();
    }
}
