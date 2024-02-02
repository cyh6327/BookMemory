package com.yh.bookMemory.controller;

import com.yh.bookMemory.jwt.JwtProperties;
import com.yh.bookMemory.jwt.JwtTokenVerifier;
import com.yh.bookMemory.service.EmailService;
import com.yh.bookMemory.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.IOException;
import java.util.Objects;

@RequestMapping("/email")
@RestController
@Log4j2
public class EmailController {

    @Autowired
    EmailService emailService;

    @Autowired
    UserService userService;

    @PostMapping("/send/{email}/{sentenceSortKey}")
    public ResponseEntity<Object> sendEmail(@PathVariable String email, @PathVariable String sentenceSortKey, HttpServletResponse response) throws IOException {
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

        if(emailService.sendMail(email, sentenceSortKey, 5)) {
            String redirectUri = "/email/send";
            response.sendRedirect(redirectUri);
        } else {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok().build();
    }

}
