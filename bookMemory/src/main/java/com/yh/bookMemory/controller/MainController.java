package com.yh.bookMemory.controller;

import com.yh.bookMemory.dto.BookInfoDTO;
import com.yh.bookMemory.dto.PageRequestDTO;
import com.yh.bookMemory.entity.BookInfo;
import com.yh.bookMemory.entity.BookSentences;
import com.yh.bookMemory.repository.BookInfoRepository;
import com.yh.bookMemory.repository.BookSentencesRepository;
import com.yh.bookMemory.service.BookService;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@Controller
@RequestMapping("/book")
@Log4j2
public class MainController {

    @Autowired
    BookService bookService;

    @Autowired
    BookInfoRepository bookInfoRepository;

    @Autowired
    BookSentencesRepository bookSentencesRepository;

    @GetMapping("/dashboard")
    public String dashboard(@ModelAttribute("requestDTO") PageRequestDTO requestDTO, Model model) throws IOException {
        log.info("dashboard.......");

        List<Long> list = LongStream.rangeClosed(1,1050).boxed().collect(Collectors.toList());
        log.info("===========list"+list.toString());

        int batchSize = 1000;

        List<List<Long>> batches = IntStream.range(0, (list.size() + batchSize - 1) / batchSize)
                .mapToObj(i -> list.subList(i * batchSize, Math.min((i + 1) * batchSize, list.size())))
                .collect(Collectors.toList());

        // batches 리스트에는 1000개씩 끊어진 리스트들이 저장됩니다.
        for (List<Long> batch : batches) {
            System.out.println(batch);
        }


//        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().page(1).size(10).build();
//        PageResultDTO<BookInfoDTO, BookInfo> resultDTO = bookService.getAllBookList(pageRequestDTO);
//
//        for(BookInfoDTO bookInfoDTO : resultDTO.getDtoList()) {
//            System.out.println(bookInfoDTO);
//        }
//        log.info("resultDTO......."+resultDTO);
//        List<BookInfoDTO> bookList = resultDTO.getDtoList();
//        log.info("bookList......."+bookList.toString());
//
//        model.addAttribute("list", bookList);

        return "/layout/index";
    }

    @GetMapping("/create")
    public String createPage() {
        log.info("move createBook Page...............");
        return "/createBook";
    }

    @GetMapping("/detail")
    public String detailPage(long bookId, @ModelAttribute("requestDTO") PageRequestDTO requestDTO, Model model) {
        log.info("detailPage...............");
        log.info("bookId..............."+bookId);

        List<BookSentences> bookSentencesList = bookSentencesRepository.findAll();

        List<String> modifiedSentences = bookSentencesList.stream()
                .map(bookSentence -> bookSentence.getSentenceText().toString().replace("<br>", "\r\n"))
                .collect(Collectors.toList());


        model.addAttribute("list",modifiedSentences);
//
//        BookSentencesDTO dto = bookService.read(bookId);
//        log.info("BookSentencesDTO................"+dto);
//
//        model.addAttribute("dto", dto);

        return "/bookDetail";
    }

    @PostMapping("/create")
    public String createBook(BookInfoDTO bookInfoDTO, RedirectAttributes redirectAttributes) {
        log.info("createBook...............");
        log.info("bookInfoDTO......."+bookInfoDTO);

        Long bookID = bookService.create(bookInfoDTO);
        log.info("bookId......"+bookID);
//        redirectAttributes.addFlashAttribute("msg",bookID);
        return "redirect:/book/dashboard";
    }

//    @PostMapping("/insertSentence")
//    public String insertSentence(BookInfoDTO bookInfoDTO) throws IOException {
//        log.info("insertSentence...............");
//        getHtmlContent();
//
//        log.info("bookInfoDTO......."+bookInfoDTO);
//
//        Long bookID = bookService.create(bookInfoDTO);
//        log.info("bookId......"+bookID);
////        redirectAttributes.addFlashAttribute("msg",bookID);
//        return "redirect:/book/dashboard";
//    }



    @PostMapping("/insertSentence")
    public String getHtmlContent(Model model) throws IOException {
        ClassPathResource resource = new ClassPathResource("test.html");
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

//        arr[1] = arr[1].replaceAll("</br>","<br>");
//        arr[1] = arr[1].replaceAll("<br>\\s*", "<br>");
//        arr[1] = arr[1].replaceAll("\\s*<br>", "<br>");
//        System.out.println(arr[1]);
//        System.out.println("===========================================");
        //arr[i] = arr[i].replaceAll("(?<=\\S)\\s*(<(/)?br>)\\s*(?=\\S)", "$2<br>$3");

        List<BookSentences> sentences = new ArrayList<>();
        for(int i=1; i<arr.length-1; i++) {
            //arr[i] = arr[i].replaceAll("(?<=\\S)\\s*(<(/)?br>)\\s*(?=\\S)", " $2<br>");
            System.out.println("===========================================");
            arr[i] = arr[i].replaceAll("</br>","<br>");
            arr[i] = arr[i].replaceAll("<br>\\s*", "<br>");
            arr[i] = arr[i].replaceAll("\\s*<br>", "<br>");
            System.out.println(arr[i]);

            BookInfo bookInfo = bookInfoRepository.getOne(1L);
            log.info("===================target bookInfo"+bookInfo.toString());

            BookSentences bookSentences = BookSentences.builder()
                            .sentenceText(arr[i])
                                    .favoriteFlag('N')
                                            .bookInfo(bookInfo)
                                                    .build();

            log.info("========================bookSentences"+bookSentences.toString());

            sentences.add(bookSentences);
            log.info("========================bookSentences list"+sentences.toString());
        }

        bookSentencesRepository.saveAll(sentences);

        return "redirect:/book/bookDetail";

//        for (String part : arr) {
//            System.out.println(part);
//            System.out.println("===============================");
//        }
    }

}
