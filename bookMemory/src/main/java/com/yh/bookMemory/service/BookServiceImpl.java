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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Log4j2
public class BookServiceImpl implements BookService, CommonService {

    @Autowired
    BookInfoRepository bookInfoRepository;

    @Autowired
    BookSentencesRepository bookSentencesRepository;

    @Override
    public BookInfo createBook(BookInfoDTO dto) {
        BookInfo entity = dtoToEntitiy(dto);

        BookInfo createdBook = bookInfoRepository.save(entity);

        return createdBook;
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
    public BookInfo getBookInfo(long bookId) {
        BookInfo bookInfo = bookInfoRepository.getOne(bookId);

        return bookInfo;
    }

//    @Override
//    public BookSentencesDTO read(Long bookId) {
//       //Optional<BookSentences> result = bookSentencesRepository.findById(bookId);
//       Optional<BookSentences> result = bookSentencesRepository.findByBookInfo_BookId(bookId);
//        return result.isPresent()? sentenceEntityToDto(result.get()) : null;
//    }
}
