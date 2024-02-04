package com.yh.bookMemory.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.yh.bookMemory.dto.BookInfoDTO;
import com.yh.bookMemory.dto.BookSentencesDTO;
import com.yh.bookMemory.entity.BookInfo;
import com.yh.bookMemory.entity.BookSentences;
import com.yh.bookMemory.entity.Users;
import com.yh.bookMemory.jwt.JwtProperties;
import com.yh.bookMemory.jwt.JwtTokenVerifier;
import com.yh.bookMemory.repository.BookInfoRepository;
import com.yh.bookMemory.repository.BookSentencesRepository;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
@Log4j2
public class BookServiceImpl implements BookService, CommonService {

    @Autowired
    private BookInfoRepository bookInfoRepository;

    @Autowired
    private BookSentencesRepository bookSentencesRepository;

    @Autowired
    private UserService userService;

    @Override
    public BookInfoDTO createBook(BookInfoDTO dto) {
        log.info("createBook.....................................");

        Object accessToken = Objects.requireNonNull(RequestContextHolder.getRequestAttributes()).getAttribute("accessToken", RequestAttributes.SCOPE_REQUEST);
        if(accessToken == null) {
            throw new NullPointerException("accessToken 값이 없습니다.");
        }

        // TODO: CommonService로 빼려고 했으나 뭔가 잘 안됨... 나중에 바꿔보자
        JwtTokenVerifier jwtTokenVerifier = new JwtTokenVerifier(JwtProperties.SECRET);
        DecodedJWT jwt = jwtTokenVerifier.verify(Objects.toString(accessToken));
        Long userKey = jwt.getClaim("user_key").asLong();
        log.info("userKey....................................."+userKey);

        //TODO: bookInfo 테이블에 user_key를 넣어야해서 일단은 Users entity를 찾아와서 넣어줬는데 조금 더 간단한 방법이 있는지 알아보고 수정
        //생각해본건 아예 RequestContextHolder에 accesstoken 대신 users entity를 넣는 방법인데 일반적으로 이렇게 하는지는 모르겠네
        Users user = userService.getUserInfoByUserKey(userKey);
        dto.setUsers(user);

        BookInfo entity = dto.toEntity();

        BookInfo createdBook = bookInfoRepository.save(entity);
        BookInfoDTO createdBookDTO = entityToDto(createdBook);

        log.info("createdBookDTO....................................."+createdBookDTO);

        return createdBookDTO;
    }

    @Override
    public List<BookInfoDTO> getAllBookList(long userKey) {
        log.info("userKey...................."+userKey);

        log.info("getAllBookList....................");

        List<BookInfo> resultList = bookInfoRepository.getReferenceByUsersUserKey(userKey);
        List<BookInfoDTO> resultListDto = new ArrayList<>();

        for(BookInfo book : resultList){
            BookInfoDTO dto = entityToDto(book);

            resultListDto.add(dto);
        }

        return resultListDto;
    }




    private static String removeExtraSpacesAfterDot(String input) {
        // 정규식 패턴: ". " 뒤의 여러 공백을 하나로 대체
        String regex = "\\.\\s+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        // 패턴과 일치하는 부분을 찾아 대체
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(result, ". ");
        }
        matcher.appendTail(result);

        return result.toString();
    }

    /*
    규칙
    1. 텍스트 파일만 가능하며 텍스트 파일명과 문장을 추가할 책 제목과 동일해야 한다.
    2. 텍스트 파일은 다른 html 태그 전부 제거하고 <hr> 태그만 남긴다. (<hr> 태그 기준으로 문장 분할)
     */
    @Override
    public List<BookSentencesDTO> insertSentenceFromFile(long bookId, String title) throws IOException {
        log.info("bookId............."+bookId);
        log.info("title............."+title);
        String filePath = Paths.get("").toAbsolutePath()+"/src/main/resources/"+title+".txt";
        Charset charset = StandardCharsets.UTF_8;
        String text = Files.readAllLines(Paths.get(filePath),charset).get(0);
        log.info("text............."+text);
        String[] splitByHr = text.split("<hr>");

        List<BookSentences> sentences = new ArrayList<>();

        for(String str : splitByHr) {
            log.info("splitByHr............."+str);
            str = str.replaceAll("<br><br>", "<br>");
            str = str.replaceAll("<br>\\s*", "<br>");
            str = str.replaceAll("\\s*<br>", "<br>");

            String processedText = removeExtraSpacesAfterDot(str.trim());
            log.info("removeBlank............."+processedText);

            BookInfo bookInfo = bookInfoRepository.getOne(bookId);

            BookSentences bookSentences = BookSentences.builder()
                    .sentenceText(processedText)
                    .favoriteFlag('N')
                    .bookInfo(bookInfo)
                    .build();

            sentences.add(bookSentences);
        }

        List<BookSentences> sentenceList = bookSentencesRepository.saveAll(sentences);

        List<BookSentencesDTO> sentenceListDto = new ArrayList<>();

        for(BookSentences sentence : sentenceList){
            BookSentencesDTO dto = sentenceEntityToDto(sentence);

            sentenceListDto.add(dto);
        }

        log.info("sentenceListDto.................."+sentenceListDto.toString());

        return sentenceListDto;
    }

    @Override
    public BookInfoDTO getBookInfo(long bookId) {
        BookInfo bookInfo = bookInfoRepository.getReferenceByBookId(bookId);
        BookInfoDTO bookInfoDto = entityToDto(bookInfo);

        return bookInfoDto;
    }

    @Override
    public List<BookSentencesDTO> getSentences(long bookId) throws IOException {
        List<BookSentences> bookSentenceList = bookSentencesRepository.findByBookInfoBookId(bookId);

        //TODO: 각 문장 앞에 별(favorite_flag) 추가하고 db업데이트 로직 구성
//        List<BookSentences> sentenceList = bookSentenceList.stream()
//                .map(bookSentence -> new BookSentences(bookSentence.getSentenceText().replace("<br>", "\r\n")))
//                .collect(Collectors.toList());

        List<BookSentencesDTO> sentenceListDto = new ArrayList<>();

        for(BookSentences sentence : bookSentenceList){
            BookSentencesDTO dto = sentenceEntityToDto(sentence);

            sentenceListDto.add(dto);
        }

        return sentenceListDto;
    }

//    @Override
//    public BookSentencesDTO read(Long bookId) {
//       //Optional<BookSentences> result = bookSentencesRepository.findById(bookId);
//       Optional<BookSentences> result = bookSentencesRepository.findByBookInfo_BookId(bookId);
//        return result.isPresent()? sentenceEntityToDto(result.get()) : null;
//    }
}
