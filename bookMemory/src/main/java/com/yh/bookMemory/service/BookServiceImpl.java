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
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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

    @Override
    public List<Map<String, String>> searchBookInfoFromYes24(String keyword) throws IOException {
        Document doc = Jsoup.connect("https://www.yes24.com/Product/Search?domain=ALL&query="+keyword).timeout(5000).get();
        Elements searchResult = doc.select("#yesSchList li[data-goods-no]");

        log.info("searchResult............................"+searchResult);

        List<Map<String, String>> bookInfoList = new ArrayList<>();

        int count = 0;

        // 상위 10개의 결과를 가져온다.
        for (Element element : searchResult) {
            Map<String, String> bookInfo = new HashMap<>();

            String bookId = element.attr("data-goods-no");

            Map<String, String> bookDesc = getDetailBookInfoFromYes24(bookId);

            String mainDesc = bookDesc.get("mainDesc");
            String subDesc = bookDesc.get("subDesc");

            String title = element.select(".itemUnit .item_info .info_row.info_name .gd_name").text();

            String subTitle = element.select(".itemUnit .item_info .info_row.info_name .gd_nameE").text();

            String author = element.select(".itemUnit .item_info .info_row.info_pubGrp .authPub.info_auth a").text();

            String publisher = element.select(".itemUnit .item_info .info_row.info_pubGrp .authPub.info_pub a").text();

            String img = element.select(".itemUnit .item_img .img_canvas .img_item .img_grp .lnk_img .img_bdr img").attr("data-original");

            bookInfo.put("bookId",bookId);
            bookInfo.put("title",title);
            bookInfo.put("subTitle",subTitle);
            bookInfo.put("mainDesc",mainDesc);
            bookInfo.put("subDesc",subDesc);
            bookInfo.put("author",author);
            bookInfo.put("publisher",publisher);
            bookInfo.put("img",img);

            log.info("current bookInfo........................"+bookInfo.toString());

            bookInfoList.add(bookInfo);

            log.info("current bookInfoList........................"+bookInfoList.toString());

            count += 1;

            if(count == 10) break;
        }

        return bookInfoList;
    }

    public Map<String, String> getDetailBookInfoFromYes24(String bookId) throws IOException {
        Document doc = Jsoup.connect("https://www.yes24.com/Product/Goods/"+bookId).timeout(5000).get();
        Elements detailDesc = doc.select("#infoset_introduce .infoSetCont_wrap .wrapTb .infoWrap_txt .infoWrap_txtInner .txtContentText");
        String detailDescHtml = detailDesc.html();

        log.info("detailDescHtml....................."+detailDescHtml);
        log.info("parsed detailDescHtml.....................");


        String[] desc = detailDescHtml.split("&lt;/\b&gt;");
        log.info("desc arr....................."+ Arrays.toString(desc)+"............end");
        String mainDesc = detailDescHtml.split("&lt;/\b&gt;")[0];
        String subDesc = detailDescHtml.split("&lt;/\b&gt;")[1];

        Map<String, String> bookDesc = new HashMap<>();
        bookDesc.put("mainDesc", mainDesc);
        bookDesc.put("subDesc", subDesc);

        log.info("bookDesc......................"+bookDesc.toString());

        return bookDesc;
    }
//    @Override
//    public BookSentencesDTO read(Long bookId) {
//       //Optional<BookSentences> result = bookSentencesRepository.findById(bookId);
//       Optional<BookSentences> result = bookSentencesRepository.findByBookInfo_BookId(bookId);
//        return result.isPresent()? sentenceEntityToDto(result.get()) : null;
//    }
}
