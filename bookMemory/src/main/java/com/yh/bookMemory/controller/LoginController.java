package com.yh.bookMemory.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.yh.bookMemory.ConfigUtils;
import com.yh.bookMemory.dto.UserDTO;
import com.yh.bookMemory.entity.Users;
import com.yh.bookMemory.jwt.CreateJwt;
import com.yh.bookMemory.jwt.JwtReturner;
import com.yh.bookMemory.service.CommonService;
import com.yh.bookMemory.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Log4j2
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Object> loginPage(@RequestBody HashMap<String,String> userData) throws GeneralSecurityException, IOException {

        log.info("userData................................."+userData);

        String email = userData.get("email");
        Users user = userService.getUserInfo(email);

        if(user == null) {
            user = userService.createUser(userData);
        }

        String accessToken = CreateJwt.createAccessToken(user);
        String refreshToken = CreateJwt.createRefreshToken(user, accessToken);

        log.info("accessToken................................."+accessToken);
        log.info("refreshToken................................."+refreshToken);

        UserDTO dto = userService.userEntityToDto(user);
        if(dto.getRefreshToken() == null) {
            HashMap<String, String> updateMap = new HashMap<>();
            updateMap.put("email", email);
            updateMap.put("refreshToken", refreshToken);
            user = userService.updateUser(updateMap);

            log.info("update refreshToken................................."+user);
        }

        ResponseCookie cookie = ResponseCookie.from("accessToken", accessToken)
                .path("/")
                .maxAge(3600)
                .build();

        HttpHeaders header = new HttpHeaders();
        header.set("Set-Cookie", cookie.toString());

        return new ResponseEntity((new JwtReturner(accessToken, refreshToken)), header, HttpStatus.valueOf(201));
    }


//    @PostMapping("/google-login")
//    public ResponseEntity googleLogin(HttpServletRequest req, @RequestBody String messageBody, Model model,
//                                      HttpServletResponse response) throws GeneralSecurityException, IOException {
//        log.info("login success....");
//
//        String[] messageList = messageBody.split("&");
//        String csrf_token_body = "";
//        String idTokenString = "";
//
//        for(String str : messageList) {
//            String[] split = str.split("=");
//            if(split[0].equals("g_csrf_token")) {
//                csrf_token_body = split[1];
//            } else if(split[0].equals("credential")) {
//                idTokenString = split[1];
//            }
//        }
//
//        String g_csrf_token = getCookie(req, "g_csrf_token");
//        log.info("g_csrf_token...."+g_csrf_token);
//        log.info("csrf_token_body...."+csrf_token_body);
//        log.info("idTokenString...."+idTokenString);
//
//        ResponseEntity<String> responseEntity = null;
//
//        if(g_csrf_token == null) {
//            responseEntity = new ResponseEntity("No CSRF token in Cookie.", HttpStatus.valueOf(400));
//        }
//
//        if(csrf_token_body == null) {
//            responseEntity = new ResponseEntity("No CSRF token in post body.", HttpStatus.valueOf(400));
//        }
//
//        if(!g_csrf_token.equals(csrf_token_body)) {
//            responseEntity = new ResponseEntity("Failed to verify double submit cookie.", HttpStatus.valueOf(400));
//        }
//
//        responseEntity = new ResponseEntity(HttpStatus.valueOf(200));
//
//        model.addAttribute("statusCode",responseEntity.getStatusCode());
//
//        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
//                // Specify the CLIENT_ID of the app that accesses the backend:
//                .setAudience(Collections.singletonList("234620413168-isfsfqgedg2tsm3g66hrb9lvd0e236g7.apps.googleusercontent.com"))
//                // Or, if multiple clients access the backend:
//                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
//                .build();
//
//        log.info("verifier.toString().....",verifier.toString());
//
//        // (Receive idTokenString by HTTPS POST)
//
//        GoogleIdToken idToken = verifier.verify(idTokenString);
//        if (idToken != null) {
//            GoogleIdToken.Payload payload = idToken.getPayload();
//
//            // Print user identifier
//            String userId = payload.getSubject();
//            log.info("User ID................." + userId);
//
//            // Get profile information from payload
//            String email = payload.getEmail();
//            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
//            String name = (String) payload.get("name");
//            String pictureUrl = (String) payload.get("picture");
//            String locale = (String) payload.get("locale");
//            String familyName = (String) payload.get("family_name");
//            String givenName = (String) payload.get("given_name");
//
//            log.info("email...."+email);
//            log.info("emailVerified...."+emailVerified);
//            log.info("name...."+name);
//            log.info("pictureUrl...."+pictureUrl);
//            log.info("locale...."+locale);
//            log.info("familyName...."+familyName);
//            log.info("givenName...."+givenName);
//
//            // Use or store profile information
//            // ...
//
//        } else {
//            log.info("Invalid ID token.....");
//        }
//
//        String redirectUri = "/layout/index";
//        response.sendRedirect(redirectUri);
//
//        return ResponseEntity.ok().build();
//    }
//
//    public String getCookie(HttpServletRequest req, String cookieName){
//        Cookie[] cookies=req.getCookies(); // 모든 쿠키 가져오기
//        if(cookies!=null){
//            for (Cookie c : cookies) {
//                String name = c.getName(); // 쿠키 이름 가져오기
//                String value = c.getValue(); // 쿠키 값 가져오기
//                if (name.equals(cookieName)) {
//                    return value;
//                }
//            }
//        }
//        return null;
//    }
}
