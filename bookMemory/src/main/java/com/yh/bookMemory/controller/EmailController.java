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

    @Autowired
    EmailService emailService;

    @Autowired
    UserService userService;

    @PostMapping("/send/{email}")
    public ResponseEntity<Object> sendEmail(@PathVariable String email, HttpServletResponse response) throws IOException {
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

        if(emailService.sendMail(email, 5)) {
            String redirectUri = "/email/send";
            response.sendRedirect(redirectUri);
        } else {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok().build();
    }

}
