package com.yh.bookMemory.controller;

import com.yh.bookMemory.dto.BookInfoDTO;
import com.yh.bookMemory.dto.PageRequestDTO;
import com.yh.bookMemory.dto.PageResultDTO;
import com.yh.bookMemory.entity.BookInfo;
import com.yh.bookMemory.entity.BookSentences;
import com.yh.bookMemory.repository.BookInfoRepository;
import com.yh.bookMemory.repository.BookSentencesRepository;
import com.yh.bookMemory.service.BookService;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
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
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

//        List<Long> list = LongStream.rangeClosed(1,1050).boxed().collect(Collectors.toList());
//        log.info("===========list"+list.toString());
//
//        int batchSize = 1000;
//
//        List<List<Long>> batches = IntStream.range(0, (list.size() + batchSize - 1) / batchSize)
//                .mapToObj(i -> list.subList(i * batchSize, Math.min((i + 1) * batchSize, list.size())))
//                .collect(Collectors.toList());
//
//        // batches 리스트에는 1000개씩 끊어진 리스트들이 저장됩니다.
//        for (List<Long> batch : batches) {
//            System.out.println(batch);
//        }

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
    public String detailPage(long bookId, String title, @ModelAttribute("requestDTO") PageRequestDTO requestDTO, Model model) {
        log.info("detailPage...............");
        log.info("bookId..............."+bookId);
        log.info("title............."+title);

//        List<BookSentences> bookSentencesList = bookSentencesRepository.findByBookInfoBookId(bookId);
//        List<BookSentences> modifiedSentences = bookSentencesList.stream()
//                .map(bookSentence -> new BookSentences(bookSentence.getSentenceText().replace("<br>", "\r\n")))
//                .collect(Collectors.toList());
//
//        log.info("bookSentencesList............"+bookSentencesList.toString());
//
//        //TODO: 각 문장 앞에 별(favorite_flag) 추가하고 db업데이트 로직 구성
//        model.addAttribute("list",modifiedSentences);

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

        //System.out.println("========================stentences[0]"+arr[0].toString());
        List<BookSentences> sentences = new ArrayList<>();
        for(int i=1; i<arr.length-1; i++) {
            //arr[i] = arr[i].replaceAll("(?<=\\S)\\s*(<(/)?br>)\\s*(?=\\S)", " $2<br>");
            System.out.println("===========================================");
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

            System.out.println("arr[i]....................."+arr[i]);
        }
        //log.info("========================bookSentences list"+sentences.toString());
        bookSentencesRepository.saveAll(sentences);

        return "redirect:/book/detail?bookId="+bookId;
    }

    @GetMapping("/sendEmail")
    public String sendEmailPage() {
        log.info("sendEmailPage..........................");
        return "/sendEmail";
    }

    @PostMapping("/sendEmail")
    public String sendEmail(String email) {
        log.info("sendEmail.........................");
        log.info("email........................."+email);

        // SMTP 서버 설정
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // SMTP 서버 주소
        props.put("mail.smtp.port", "587"); // SMTP 포트 (587 또는 465)
        props.put("mail.smtp.auth", "true"); // 인증 필요 여부
        props.put("mail.smtp.starttls.enable", "true"); // TLS 사용 여부 (필요 시)

        // 계정 정보
        String username = "cyh6327@gmail.com";
        String password = "iiyf dpuk idze aipg";

        // 세션 생성
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // 이메일 메시지 생성
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username)); // 발신자 주소
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email)); // 수신자 주소
            message.setSubject("랜덤 문장 5개"); // 이메일 제목
            //TODO: 문장 5개 셀렉트 해오기
            //pickRandomSentences()
            //message.setContent('', "text/html");

            // 이메일 보내기
            Transport.send(message);

            System.out.println("이메일이 성공적으로 전송되었습니다.");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("이메일 전송 중 오류가 발생했습니다.");
        }

        return "redirect:/book/dashboard";
    }

//    public pickRandomSentences() {
//
//    }
}

