package com.yh.bookMemory.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.yh.bookMemory.dto.BookInfoDTO;
import com.yh.bookMemory.dto.PageRequestDTO;
import com.yh.bookMemory.dto.PageResultDTO;
import com.yh.bookMemory.entity.BookInfo;
import com.yh.bookMemory.entity.BookSentences;
import com.yh.bookMemory.repository.BookInfoRepository;
import com.yh.bookMemory.repository.BookSentencesRepository;
import com.yh.bookMemory.service.BookService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.springframework.web.util.WebUtils.getCookie;

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

    @GetMapping("/login")
    public String loginPage() {
        log.info("move loginPage...............");
        return "/login";
    }

    @PostMapping("/google-login")
    public String googleLogin(HttpServletRequest req, @RequestBody String messageBody, Model model) throws GeneralSecurityException, IOException {
        log.info("login success....");

        String[] messageList = messageBody.split("&");
        String csrf_token_body = "";
        String idTokenString = "";

        for(String str : messageList) {
            String[] split = str.split("=");
            if(split[0].equals("g_csrf_token")) {
                csrf_token_body = split[1];
            } else if(split[0].equals("credential")) {
                idTokenString = split[1];
            }
        }

        String g_csrf_token = getCookie(req, "g_csrf_token");
        log.info("g_csrf_token...."+g_csrf_token);
        log.info("csrf_token_body...."+csrf_token_body);
        log.info("idTokenString...."+idTokenString);

        ResponseEntity<String> responseEntity = null;

        if(g_csrf_token == null) {
            responseEntity = new ResponseEntity("No CSRF token in Cookie.", HttpStatus.valueOf(400));
        }

        if(csrf_token_body == null) {
            responseEntity = new ResponseEntity("No CSRF token in post body.", HttpStatus.valueOf(400));
        }

        if(!g_csrf_token.equals(csrf_token_body)) {
            responseEntity = new ResponseEntity("Failed to verify double submit cookie.", HttpStatus.valueOf(400));
        }

        responseEntity = new ResponseEntity(HttpStatus.valueOf(200));

        model.addAttribute("statusCode",responseEntity.getStatusCode());

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList("234620413168-isfsfqgedg2tsm3g66hrb9lvd0e236g7.apps.googleusercontent.com"))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();

        log.info("verifier.toString().....",verifier.toString());

        // (Receive idTokenString by HTTPS POST)

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            // Print user identifier
            String userId = payload.getSubject();
            System.out.println("User ID: " + userId);

            // Get profile information from payload
            String email = payload.getEmail();
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");

            log.info("email...."+email);
            log.info("emailVerified...."+emailVerified);
            log.info("name...."+name);
            log.info("pictureUrl...."+pictureUrl);
            log.info("locale...."+locale);
            log.info("familyName...."+familyName);
            log.info("givenName...."+givenName);

            // Use or store profile information
            // ...

        } else {
            System.out.println("Invalid ID token.");
        }
        return "/layout/index";
    }

    public String getCookie(HttpServletRequest req, String cookieName){
        Cookie[] cookies=req.getCookies(); // 모든 쿠키 가져오기
        if(cookies!=null){
            for (Cookie c : cookies) {
                String name = c.getName(); // 쿠키 이름 가져오기
                String value = c.getValue(); // 쿠키 값 가져오기
                if (name.equals(cookieName)) {
                    return value;
                }
            }
        }
        return null;
    }

    @GetMapping("/create")
    public String createPage() {
        log.info("move createBook Page...............");
        return "/createBook";
    }

    @GetMapping("/detail")
    public String detailPage(long bookId, String title, @ModelAttribute("requestDTO") PageRequestDTO requestDTO, Model model) {
        log.info("detailPage...............");
        log.info("bookId..............."+bookId);
        log.info("title............."+title);

        List<BookSentences> bookSentencesList = bookSentencesRepository.findByBookInfoBookId(bookId);
        List<BookSentences> modifiedSentences = bookSentencesList.stream()
                .map(bookSentence -> new BookSentences(bookSentence.getSentenceText().replace("<br>", "\r\n")))
                .collect(Collectors.toList());

        log.info("bookSentencesList............"+bookSentencesList.toString());

        //TODO: 각 문장 앞에 별(favorite_flag) 추가하고 db업데이트 로직 구성
        model.addAttribute("list",modifiedSentences);

        return "/bookDetail";
    }

    @PostMapping("/create")
    public String createBook(BookInfoDTO bookInfoDTO) {
        log.info("createBook...............");

        log.info("bookInfoDTO......."+bookInfoDTO);

        Long bookID = bookService.create(bookInfoDTO);
        log.info("bookId......"+bookID);
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
        return null;
    }

    @PostMapping("/insertSentenceFromFile")
    public String insertSentenceFromFile(long bookId, String title, Model model) throws IOException {
        log.info("bookId...................."+bookId);
        log.info("title...................."+title);
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

        System.out.println("========================stentences[0]"+arr[0].toString());
        List<BookSentences> sentences = new ArrayList<>();
        for(int i=1; i<arr.length-1; i++) {
            //arr[i] = arr[i].replaceAll("(?<=\\S)\\s*(<(/)?br>)\\s*(?=\\S)", " $2<br>");
            System.out.println("===========================================");
            arr[i] = arr[i].replaceAll("</br>","<br>");
            arr[i] = arr[i].replaceAll("<br>\\s*", "<br>");
            arr[i] = arr[i].replaceAll("\\s*<br>", "<br>");
            System.out.println(arr[i]);

            BookInfo bookInfo = bookInfoRepository.getOne(bookId);

            BookSentences bookSentences = BookSentences.builder()
                    .sentenceText(arr[i])
                    .favoriteFlag('N')
                    .bookInfo(bookInfo)
                    .build();

            sentences.add(bookSentences);

        }
        log.info("========================bookSentences list"+sentences.toString());
        bookSentencesRepository.saveAll(sentences);

        return "redirect:/book/detail?bookId="+bookId;

//        for (String part : arr) {
//            System.out.println(part);
//            System.out.println("===============================");
//        }
    }

}
