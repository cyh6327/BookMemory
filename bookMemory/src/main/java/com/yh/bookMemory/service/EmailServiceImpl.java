package com.yh.bookMemory.service;

import com.yh.bookMemory.dto.BookSentencesDTO;
import com.yh.bookMemory.dto.UserDTO;
import com.yh.bookMemory.entity.BookSentences;
import com.yh.bookMemory.entity.Users;
import com.yh.bookMemory.jwt.JwtProperties;
import com.yh.bookMemory.jwt.JwtTokenVerifier;
import com.yh.bookMemory.repository.BookSentencesCustomRepository;
import com.yh.bookMemory.repository.BookSentencesRepository;
import com.yh.bookMemory.repository.SentenceLogRepository;
import com.yh.bookMemory.repository.UserRepository;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Stream;

@Service
@Log4j2
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService, CommonService {
    @Autowired
    BookSentencesRepository bookSentencesRepository;

    @Autowired
    BookService bookService;

    @Autowired
    UserService userService;

    @Autowired
    SentenceLogRepository sentenceLogRepository;

    @Autowired
    BookSentencesCustomRepository bookSentencesCustomRepository;

    @Autowired
    UserRepository userRepository;

    private final JavaMailSender javaMailSender;

    @Value("${spring.datasource.emailId}")
    private String username;

    @Value("${spring.datasource.emailPw}")
    private String password;

    @Override
    public List<BookSentencesDTO> pickRandomSentences(String sentenceSortKey, long sendCnt, int limit) throws Exception {
        log.info("limit...................."+limit);
        log.info("랜덤 문장 선정.....................................");

        String[] sentenceSortKeyList = sentenceSortKey.split(",");
        Long[] parsedSentenceSortKeyList = Stream.of(sentenceSortKeyList).mapToLong(Long::parseLong).boxed().toArray(Long[]::new);
        List<BookSentencesDTO> sentencesList = new ArrayList<>();

        for(int i=0; i<limit; i++) {
            BookSentences sentence = null;
            if(sentenceSortKeyList.length != 0) {
                sentence = bookSentencesRepository.findByBookSentenceId(parsedSentenceSortKeyList[i+sendCnt]);
            } else {
                sentence = bookSentencesRepository.findByBookSentenceId((long) i+sendCnt);
            }
            sentencesList.add(sentenceEntityToDto(sentence));
        }

        log.info("sentencesList.");

        //List<BookSentences> sentenceList = bookSentencesRepository.pickRandomSentences(sortKey, sendCnt, limit);
        //List<BookSentences> sentenceList = bookSentencesCustomRepository.pickRandomSentences(sortKey, 0, limit);




//        Date today = new Date();
//        SentenceLogDTO sentenceLogDTO = SentenceLogDTO.builder()
//                .sendDate(today)
//                .bookSentences()
//
//        SentenceLog logEntity =
//                SentenceLog
//        sentenceLogRepository.save()



//        // 대칭키 생성
//        SecretKey secretKey = generateSecretKey();
//
//        // 데이터 암호화
//        String encryptedData = encryptData(str, secretKey);
//
//        System.out.println("Original Data: " + str);
//        System.out.println("Encrypted Data: " + encryptedData);
//
//        // 데이터 복호화
//        String decryptedData = decryptData(encryptedData, secretKey);
//        System.out.println("Decrypted Data: " + decryptedData);

        //return sentenceListDto;
       return null;
    }

    @Override
    public boolean sendMail(String email, String sentenceSortKey, int limit) {

        log.info("sendMail Service start ...................");
        log.info("sentenceSortKey ..................." + sentenceSortKey);

        Object accessToken = Objects.requireNonNull(RequestContextHolder.getRequestAttributes()).getAttribute("accessToken", RequestAttributes.SCOPE_REQUEST);
        if(accessToken == null) {
            throw new NullPointerException("accessToken 값이 없습니다.");
        }

        JwtTokenVerifier jwtTokenVerifier = new JwtTokenVerifier(JwtProperties.SECRET);
        Object jwtInfo = jwtTokenVerifier.getJwtInfo(Objects.toString(accessToken), "user_key");

        if(jwtInfo == null) {
            throw new NullPointerException("user_key 값이 없습니다.");
        }

        Long userKey = Long.parseLong(jwtInfo.toString());

        Users receiver =  userService.getUserInfoByUserKey(userKey);
        UserDTO receiverDTO = receiver.toDTO();

        int sendCnt = receiverDTO.getSendCnt();

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

            // TODO: sortKey 파라미터로 넘어온 것으로 세팅 해서 넘기기
            String sortKey = "";
            List<BookSentencesDTO> sentenceList = pickRandomSentences(sortKey, sendCnt, limit);

            StringBuilder layoutBuilder = new StringBuilder();

            String header = "<div role=\"article\" lang=\"en\" style=\"padding:20px 0px;margin:0 auto;background:#ffffff\"><table style=\"width:100%;background:#ffffff\" border=\"0\"><tbody><tr><td align=\"center\"><div style=\"width:100%;max-width:630px;background:#ffffff;margin:0px auto\">";

            StringBuilder bodybuilder = new StringBuilder();

//            for(BookSentencesDTO dto : sentenceList) {
//                String bookSentence = dto.getSentenceText();
//                Long bookId = dto.getBookInfo().getBookId();
//                String bookTitle = bookService.getBookInfo(bookId).getTitle();
//                String author = bookService.getBookInfo(bookId).getAuthor();
//
//                String sentenceSection = "<div style=\"border:3px solid rgba(208,173,240,0.3);border-radius: 20px;margin: 50px 0;\"><div style=\"word-break:break-all;text-align:left;margin:0px;line-height:1.7;word-break:break-word;font-size:14px;font-family:noto sans kr,noto sans cjk kr,noto sans cjk,Malgun Gothic,apple sd gothic neo,nanum gothic,malgun gothic,dotum,arial,helvetica,Meiryo,MS Gothic,sans-serif!important;color:#000000;padding:30px\"><span style=\"padding:0;list-style-type:none\">"+bookSentence+"</span></div><div style=\"padding:0 30px 30px;\"><span style=\"color:#888888\">- ["+bookTitle+"] "+author+"</span></div></div>";
//
//                bodybuilder.append(sentenceSection);
//            }

            String body = bodybuilder.toString();

            String footer = "</div></td></tr></tbody></table></div>";

            String result = layoutBuilder.append(header).append(body).append(footer).toString();

            message.setContent(result, "text/html;charset=euc-kr");

            // 이메일 보내기
            Transport.send(message);

            log.info("이메일이 성공적으로 전송되었습니다.");

            // user sendCnt 컬럼 업데이트
            receiverDTO.setSendCnt(sendCnt + limit);
            userRepository.saveAndFlush(receiverDTO.toEntity());

            return true;
        } catch (MessagingException e) {
            log.info("이메일 전송 중 오류가 발생했습니다.");
            return false;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
