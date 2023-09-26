package com.yh.bookMemory.controller;

import com.yh.bookMemory.dto.BookInfoDTO;
import com.yh.bookMemory.dto.BookSentencesDTO;
import com.yh.bookMemory.dto.PageRequestDTO;
import com.yh.bookMemory.dto.PageResultDTO;
import com.yh.bookMemory.entity.BookInfo;
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

@Controller
@RequestMapping("/book")
@Log4j2
public class MainController {

    @Autowired
    BookService bookService;

    @GetMapping("/dashboard")
    public String dashboard(@ModelAttribute("requestDTO") PageRequestDTO requestDTO, Model model) throws IOException {
        log.info("dashboard.......");

        getHtmlContent();

        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().page(1).size(10).build();
        PageResultDTO<BookInfoDTO, BookInfo> resultDTO = bookService.getAllBookList(pageRequestDTO);

        for(BookInfoDTO bookInfoDTO : resultDTO.getDtoList()) {
            System.out.println(bookInfoDTO);
        }
        log.info("resultDTO......."+resultDTO);
        List<BookInfoDTO> bookList = resultDTO.getDtoList();
        log.info("bookList......."+bookList.toString());

        model.addAttribute("list", bookList);

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

        BookSentencesDTO dto = bookService.read(bookId);
        log.info("BookSentencesDTO................"+dto);

        model.addAttribute("dto", dto);

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

    public void getHtmlContent() throws IOException {
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

        List<String> sentences = new ArrayList<>();
        for(int i=1; i<arr.length-1; i++) {
            //arr[i] = arr[i].replaceAll("(?<=\\S)\\s*(<(/)?br>)\\s*(?=\\S)", " $2<br>");
            System.out.println("===========================================");
            arr[i] = arr[i].replaceAll("</br>","<br>");
            arr[i] = arr[i].replaceAll("<br>\\s*", "<br>");
            arr[i] = arr[i].replaceAll("\\s*<br>", "<br>");
            System.out.println(arr[i]);

            sentences.add(arr[i]);
        }

        System.out.println(sentences.toString());
//        for (String part : arr) {
//            System.out.println(part);
//            System.out.println("===============================");
//        }
    }

}
