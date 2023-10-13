package com.yh.bookMemory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Properties;

@RequestMapping("/email")
@RestController
@Log4j2
public class EmailController {

    @Value("${google.client.id}")
    private String username;

    @Value("${google.client.pw}")
    private String password;

    @GetMapping("/send")
    public ModelAndView sendEmailPage() {
        log.info("sendEmailPage..........................");

        return new ModelAndView("sendEmail");
    }

    @PostMapping("/send")
    public ResponseEntity sendEmail(String email, HttpServletResponse response) {
        log.info("sendEmail.........................");

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
            message.setSubject("랜덤 문장 5개"); // 이메일 제목
            message.setText("랜덤 문장 내용");
            //TODO: 문장 5개 셀렉트 해오기
            //pickRandomSentences()
            //message.setContent('', "text/html");

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
