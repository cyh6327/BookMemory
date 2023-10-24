package com.yh.bookMemory.controller;

import com.yh.bookMemory.dto.BookInfoDTO;
import com.yh.bookMemory.dto.BookSentencesDTO;
import com.yh.bookMemory.dto.PageRequestDTO;
import com.yh.bookMemory.dto.PageResultDTO;
import com.yh.bookMemory.entity.BookInfo;
import com.yh.bookMemory.entity.BookSentences;
import com.yh.bookMemory.repository.BookSentencesRepository;
import com.yh.bookMemory.service.BookService;
import com.yh.bookMemory.service.CommonService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RequestMapping("/book")
@RestController
@Log4j2
public class BookController {
    @Autowired
    BookService bookService;

    @Autowired
    CommonService commonService;

    @Autowired
    BookSentencesRepository bookSentencesRepository;

    @GetMapping("")
    public ResponseEntity<List<BookInfoDTO>> dashboard() {
        log.info("dashboard.......");

        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().page(1).size(10).build();
        PageResultDTO<BookInfoDTO, BookInfo> resultDTO = bookService.getAllBookList(pageRequestDTO);

        log.info("resultDTO......."+resultDTO.toString());

        if(resultDTO == null) {
            return ResponseEntity.notFound().build();
        }

        List<BookInfoDTO> bookList = resultDTO.getDtoList();

        return ResponseEntity.ok(bookList);
    }

    @GetMapping("/test")
    public String Test() {
        return "vue connect test";
    }

    @PostMapping("/create")
    public ResponseEntity<BookInfoDTO> createBook(@RequestBody BookInfoDTO bookInfoDTO) throws IOException {
        log.info("createBook...............");

        BookInfoDTO createdBook = bookService.createBook(bookInfoDTO);

        if(createdBook == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(createdBook);
    }

    @GetMapping("/detail/{bookId}")
    public ResponseEntity<Map<String, Object>> bookDetailPage(@ModelAttribute("requestDTO") PageRequestDTO requestDTO, @PathVariable Long bookId,
                                                               HttpServletResponse response) throws IOException {
        log.info("move bookDetailPage...............");

        // 책 정보 가져오기
        BookInfoDTO bookInfo = bookService.getBookInfo(bookId);

        if(bookInfo == null) {
            return ResponseEntity.notFound().build();
        }

        // 책 문장 가져오기
        List<BookSentencesDTO> sentenceList = bookService.getSentences(bookId);

        Map<String, Object> map = new HashMap<>();
        map.put("bookInfo",bookInfo);
        map.put("bookSentences",sentenceList);

        return ResponseEntity.ok(map);
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
