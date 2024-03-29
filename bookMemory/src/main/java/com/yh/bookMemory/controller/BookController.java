package com.yh.bookMemory.controller;

import com.yh.bookMemory.dto.BookInfoDTO;
import com.yh.bookMemory.dto.BookSentencesDTO;
import com.yh.bookMemory.dto.PageRequestDTO;
import com.yh.bookMemory.jwt.JwtProperties;
import com.yh.bookMemory.jwt.JwtTokenVerifier;
import com.yh.bookMemory.repository.BookSentencesRepository;
import com.yh.bookMemory.service.BookService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RequestMapping("/book")
@RestController
@Log4j2
public class BookController {
    @Autowired
    BookService bookService;

    @Autowired
    BookSentencesRepository bookSentencesRepository;

    @GetMapping("")
    public ResponseEntity<List<BookInfoDTO>> dashboard(HttpServletRequest request){
        log.info("dashboard.......");

        log.info("search cookie.............");
        Cookie[] cookies = request.getCookies();
        String accessToken = "";
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();

                }
            }
        }
        log.info("find accessToken......."+accessToken);

        List<BookInfoDTO> resultListDto = new ArrayList<>();

        //Object accessToken = RequestContextHolder.getRequestAttributes().getAttribute("accessToken", RequestAttributes.SCOPE_REQUEST);
        //log.info("first accessToken......."+accessToken);

        // savedToken 이 없다는 것은 최초 로그인 전 상태이다.
        if(accessToken == null) {
            log.info("최초 로그인 하기 전...................................");
            return ResponseEntity.noContent().build();
            //return ResponseEntity.ok(resultListDto);
        }

        JwtTokenVerifier jwtTokenVerifier = new JwtTokenVerifier(JwtProperties.SECRET);
        Object userKey = jwtTokenVerifier.getJwtInfo(Objects.toString(accessToken), "user_key");

        if(userKey == null) {
            log.info("userKey from jwt token is null...................................");
            return ResponseEntity.notFound().build();
        }

        log.info("userKey......."+userKey.toString());

//        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().page(1).size(10).build();
//        PageResultDTO<BookInfoDTO, BookInfo> resultDTO = bookService.getAllBookList(pageRequestDTO);

        // TODO: 타입 변환이 너무 복잡한데 간결하게 할 방법 생각
        resultListDto = bookService.getAllBookList(Long.parseLong(userKey.toString()));

        log.info("resultListDto......."+resultListDto);

        if(resultListDto == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(resultListDto);
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
    public ResponseEntity<List<BookSentencesDTO>> insertSentenceFromFile(@PathVariable(value = "bookId") Long bookId, @PathVariable(value = "title") String title,
                                                 HttpServletResponse response) throws IOException {
        List<BookSentencesDTO> sentenceListDto = bookService.insertSentenceFromFile(bookId, title);

        if(sentenceListDto == null || sentenceListDto.size() == 0) {
            return ResponseEntity.notFound().build();
        }

        String redirectUri = "/book/detail/"+bookId;
        response.sendRedirect(redirectUri);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/search/yes24/{keyword}")
    public ResponseEntity<List<Map<String, String>>> searchBookInfoFromYes24 (@PathVariable(value = "keyword") String keyword) throws IOException {
        log.info("searchBookInfoFromYes24........................keyword :"+keyword);

        List<Map<String, String>> bookInfoList =  bookService.searchBookInfoFromYes24(keyword);

        log.info("bookInfoList............................"+bookInfoList.toString());

        return ResponseEntity.ok(bookInfoList);
    }
}
