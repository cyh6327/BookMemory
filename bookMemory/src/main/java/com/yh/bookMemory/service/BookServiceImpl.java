package com.yh.bookMemory.service;

import com.yh.bookMemory.dto.BookInfoDTO;
import com.yh.bookMemory.dto.BookSentencesDTO;
import com.yh.bookMemory.dto.PageRequestDTO;
import com.yh.bookMemory.dto.PageResultDTO;
import com.yh.bookMemory.entity.BookInfo;
import com.yh.bookMemory.entity.BookSentences;
import com.yh.bookMemory.entity.Users;
import com.yh.bookMemory.jwt.JwtTokenVerifier;
import com.yh.bookMemory.repository.BookInfoRepository;
import com.yh.bookMemory.repository.BookSentencesRepository;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
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
        Long userKey = getUserKeyFromJwt();
        log.info("userKey....................................."+userKey);
        /////// getUserKeyFromJwt() 공통함수로 분리
//        String accessToken = Objects.requireNonNull(RequestContextHolder.getRequestAttributes().getAttribute("accessToken", RequestAttributes.SCOPE_REQUEST)).toString();
//        log.info("accessToken from RequestContextHolder....................................."+accessToken);
//
//        JwtTokenVerifier jwtTokenVerifier = new JwtTokenVerifier(accessToken);
//        Long userKey = Long.parseLong(jwtTokenVerifier.getJwtInfo(accessToken, "user_key"));

        //TODO: bookInfo 테이블에 user_key를 넣어야해서 일단은 Users entity를 찾아와서 넣어줬는데 조금 더 간단한 방법이 있는지 알아보고 수정
        //생각해본건 아예 RequestContextHolder에 accesstoken 대신 users entity를 넣는 방법인데 일반적으로 이렇게 하는지는 모르겠네
        Users user = userService.getUserInfoByUserKey(userKey);
        dto.setUsers(user);

        BookInfo entity = dtoToEntitiy(dto);

        BookInfo createdBook = bookInfoRepository.save(entity);
        BookInfoDTO createdBookDTO = entityToDto(createdBook);

        log.info("createdBookDTO....................................."+createdBookDTO);

        return createdBookDTO;
    }

    @Override
    public List<BookInfoDTO> getAllBookList() {
        Long userKey = getUserKeyFromJwt();
        if(userKey == null) {
            return null;
        }

        log.info("getAllBookList....................");

        List<BookInfo> resultList = bookInfoRepository.getReferenceByUsersUserKey(userKey);

        List<BookInfoDTO> resultListDto = new ArrayList<>();

        for(BookInfo book : resultList){
            BookInfoDTO dto = entityToDto(book);

            resultListDto.add(dto);
        }

        return resultListDto;
//        Pageable pageable = requestDTO.getPageable(Sort.by("bookId").descending());
//        Page<BookInfo> result = bookInfoRepository.getReferenceByUsersUserKey(userKey);
//        //Page<BookInfo> result = bookInfoRepository.findAll(pageable);
//        Function<BookInfo, BookInfoDTO> fn = (entity -> entityToDto(entity));
//
//        return new PageResultDTO<>(result,fn);
    }

    @Override
    public List<BookSentencesDTO> insertSentenceFromFile(long bookId, String title) throws IOException {
        log.info("bookId............."+bookId);
        log.info("title............."+title);
        ClassPathResource resource = new ClassPathResource(title + ".html");
        File input = resource.getFile();
        Document doc = null;

        // url에 접속한다.
        try {
            doc = Jsoup.parse(input, "UTF-8");
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }

        Elements div = doc.select("div");
        String withoutDiv = div.html().replaceAll("<div>","").replaceAll("</div>","").replaceAll("&nbsp;","");

        String[] arr = withoutDiv.split("<hr>");

        List<BookSentences> sentences = new ArrayList<>();

        for(int i=1; i<arr.length-1; i++) {
            arr[i] = arr[i].replaceAll("</br>","<br>");
            arr[i] = arr[i].replaceAll("<br>\\s*", "<br>");
            arr[i] = arr[i].replaceAll("\\s*<br>", "<br>");

            String regex = ".*\\S+.*"; // 공백을 제외한 어떠한 글자라도 존재하는 패턴
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(arr[i]);

            BookInfo bookInfo = bookInfoRepository.getOne(bookId);

            if(matcher.matches()) {
                BookSentences bookSentences = BookSentences.builder()
                        .sentenceText(arr[i])
                        .favoriteFlag('N')
                        .bookInfo(bookInfo)
                        .build();

                sentences.add(bookSentences);
            } else {
                System.out.println("empty....................");
            }
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
        BookInfo bookInfo = bookInfoRepository.getOne(bookId);
        BookInfoDTO bookInfoDto = entityToDto(bookInfo);

        return bookInfoDto;
    }

    @Override
    public List<BookSentencesDTO> getSentences(long bookId) throws IOException {
        List<BookSentences> bookSentenceList = bookSentencesRepository.findByBookInfoBookId(bookId);

        //TODO: 각 문장 앞에 별(favorite_flag) 추가하고 db업데이트 로직 구성
        List<BookSentences> sentenceList = bookSentenceList.stream()
                .map(bookSentence -> new BookSentences(bookSentence.getSentenceText().replace("<br>", "\r\n")))
                .collect(Collectors.toList());

        List<BookSentencesDTO> sentenceListDto = new ArrayList<>();

        for(BookSentences sentence : sentenceList){
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
