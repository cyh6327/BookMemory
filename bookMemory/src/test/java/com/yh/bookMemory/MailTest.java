package com.yh.bookMemory;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.Properties;


@SpringBootTest
public class MailTest {

    @Test
    public void testSendGmail() {
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
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("cyh6327@gmail.com")); // 수신자 주소
            message.setSubject("제목"); // 이메일 제목
            message.setText("이메일 내용입니다."); // 이메일 내용

            // 이메일 보내기
            Transport.send(message);

            System.out.println("이메일이 성공적으로 전송되었습니다.");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("이메일 전송 중 오류가 발생했습니다.");
        }
    }
    
}
