package com.yh.bookMemory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yh.bookMemory.dto.BookSentencesDTO;
import com.yh.bookMemory.entity.BookSentences;
import com.yh.bookMemory.entity.Users;
import com.yh.bookMemory.jwt.JwtProperties;
import com.yh.bookMemory.jwt.JwtTokenVerifier;
import com.yh.bookMemory.service.BookService;
import com.yh.bookMemory.service.EmailService;
import com.yh.bookMemory.service.UserService;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

@RequestMapping("/email")
@RestController
@Log4j2
public class EmailController {

    @Value("${spring.datasource.emailId}")
    private String username;

    @Value("${spring.datasource.emailPw}")
    private String password;

    @Autowired
    EmailService emailService;

    @Autowired
    BookService bookService;

    @Autowired
    UserService userService;

    @PostMapping("/send/{email}")
    public ResponseEntity sendEmail(@PathVariable String email, HttpServletResponse response) {
        log.info("sendEmail.........................");

        Object accessToken = RequestContextHolder.getRequestAttributes().getAttribute("accessToken", RequestAttributes.SCOPE_REQUEST);
        log.info("acceeToken............................"+accessToken);

        String sendUserName = null;
        if(accessToken != null) {
            JwtTokenVerifier jwtTokenVerifier = new JwtTokenVerifier(JwtProperties.SECRET);
            sendUserName = jwtTokenVerifier.getJwtInfo(Objects.toString(accessToken), "user_name").toString();
            log.info("sendUserName..................."+sendUserName);
//
//            log.info("userKey............................"+userKey);
////            Users sendUser = userService.getUserInfoByUserKey(Long.parseLong(userKey.toString()));
////            log.info("sendUser name"+);
        }



        // SMTP 서버 설정
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // SMTP 서버 주소
        props.put("mail.smtp.port", "587"); // SMTP 포트 (587 또는 465)
        props.put("mail.smtp.auth", "true"); // 인증 필요 여부
        props.put("mail.smtp.starttls.enable", "true"); // TLS 사용 여부 (필요 시)

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
            message.setSubject("오늘의 문장이 도착했습니다 :D"); // 이메일 제목

            List<BookSentencesDTO> sentenceList = emailService.pickRandomSentences(5);
            //List<String> sentenceContentList = sentenceList.stream().map(BookSentencesDTO::getSentenceText).toList();

            StringBuilder layoutBuilder = new StringBuilder();

            String header = "<div role=\"article\" lang=\"en\" style=\"padding:20px 0px;margin:0 auto;background:#ffffff\"><table style=\"width:100%;background:#ffffff\" border=\"0\"><tbody><tr><td align=\"center\"><div style=\"width:100%;max-width:630px;background:#ffffff;margin:0px auto\">";

            StringBuilder bodybuilder = new StringBuilder();

            for(BookSentencesDTO dto : sentenceList) {
                String bookSentence = dto.getSentenceText();
                Long bookId = dto.getBookInfo().getBookId();
                String bookTitle = bookService.getBookInfo(bookId).getTitle();
                String author = bookService.getBookInfo(bookId).getAuthor();

                String sentenceSection = "<table role=\"presentation\" style=\"width:100%;background:#fbfbfb;border:0\" cellpadding=\"0\" cellspacing=\"0\"><tbody><tr><td style=\"word-break:break-all;text-align:left;margin:0px;line-height:1.7;word-break:break-word;font-size:14px;font-family:noto sans kr,noto sans cjk kr,noto sans cjk,Malgun Gothic,apple sd gothic neo,nanum gothic,malgun gothic,dotum,arial,helvetica,Meiryo,MS Gothic,sans-serif!important;color:#000000;padding:30px\"><span style=\"padding:0;  list-style-type: none;\">"
                        +bookSentence+"</span></td></tr></tbody></table>";
                String authorSection = "<table style=\"width:100%;background:#fbfbfb;border:0\" cellpadding=\"0\" cellspacing=\"0\"><tbody><tr><td style=\"word-break:break-all;text-align:left;margin:0px;line-height:1.7;word-break:break-word;font-size:14px;font-family:noto sans kr,noto sans cjk kr,noto sans cjk,Malgun Gothic,apple sd gothic neo,nanum gothic,malgun gothic,dotum,arial,helvetica,Meiryo,MS Gothic,sans-serif!important;color:#000000;padding:0 30px 30px 30px;border-bottom: 3px solid rgba(208,173,240,0.5);\"><div style=\"text-align:left\"><span style=\"color:#888888;\">- ["
                +bookTitle+"] "+author+"</span></div></td></tr></tbody></table>";


                bodybuilder.append(sentenceSection).append(authorSection);
            }

            String body = bodybuilder.toString();

            String footer = "</div></td></tr></tbody></table></div>";

            String result = layoutBuilder.append(header).append(body).append(footer).toString();

            message.setContent(result, "text/html;charset=euc-kr");

//            List<String> mailContentList = new ArrayList<>();
//            mailContentList.add("<h2>"+sendUserName+"님을 위한 오늘의 문장</h2><br>");
//            for(BookSentencesDTO dto : sentenceList) {
//                String bookSentence = dto.getSentenceText();
//                Long bookId = dto.getBookInfo().getBookId();
//                String bookTitle = bookService.getBookInfo(bookId).getTitle();
//                String author = bookService.getBookInfo(bookId).getAuthor();
//
//                String content = bookSentence + "<br><br>" + "["+bookTitle+"] " + author;
//                mailContentList.add(content);
//            }
//
//            String mailContent = String.join("<br><br>",mailContentList);
//            log.info("mailContent........................"+mailContent);
//            message.setContent(mailContent, "text/html;charset=euc-kr");

            // 이메일 보내기
            Transport.send(message);

            log.info("이메일이 성공적으로 전송되었습니다.");

            String redirectUri = "/email/send";
            response.sendRedirect(redirectUri);

        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            log.info("이메일 전송 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok().build();
    }

}
