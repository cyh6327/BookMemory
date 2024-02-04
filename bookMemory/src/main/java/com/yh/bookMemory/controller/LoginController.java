package com.yh.bookMemory.controller;

import com.yh.bookMemory.dto.BookSentencesDTO;
import com.yh.bookMemory.dto.UserDTO;
import com.yh.bookMemory.entity.BookSentences;
import com.yh.bookMemory.entity.Users;
import com.yh.bookMemory.jwt.CreateJwt;
import com.yh.bookMemory.repository.BookSentencesRepository;
import com.yh.bookMemory.service.CommonService;
import com.yh.bookMemory.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

@RestController
@Log4j2
public class LoginController implements CommonService {

    @Autowired
    private UserService userService;

    @Autowired
    private BookSentencesRepository bookSentencesRepository;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginPage(@RequestBody HashMap<String,String> userData) throws GeneralSecurityException, IOException {

        log.info("userData................................."+userData);

        String email = userData.get("email");
        Long userKey = 1L;
        Users user = userService.getUserInfoByUserEmail(email);

        if(user == null) {
            user = userService.createUser(userData);
        }

        String accessToken = CreateJwt.createAccessToken(user);
        String refreshToken = CreateJwt.createRefreshToken(user, accessToken);

        log.info("accessToken................................."+accessToken);
        log.info("refreshToken................................."+refreshToken);

        UserDTO dto = userService.userEntityToDto(user);
        if(dto.getRefreshToken() == null) {
            user = userService.updateRefreshToken(refreshToken, userKey);

            log.info("update refreshToken................................."+user);
        }

        ResponseCookie cookie = ResponseCookie.from("accessToken", accessToken)
                .path("/")
                .maxAge(3600)
                .sameSite("none")
                .secure(true)
                .build();

        HttpHeaders header = new HttpHeaders();
        header.set("Set-Cookie", cookie.toString());

        // 책 문장키 랜덤 정렬한 배열 생성 후 로컬스토리지에 저장
        List<BookSentences> allSentenceList = bookSentencesRepository.findAll();

        String sentenceSortKey = "";

        if(!allSentenceList.isEmpty()) {
            List<BookSentencesDTO> allSentenceListDto = new ArrayList<>();
            for(BookSentences sentence : allSentenceList){
                BookSentencesDTO sentencesDTO = sentenceEntityToDto(sentence);

                allSentenceListDto.add(sentencesDTO);
            }

            List<String> allSentenceIdList = new ArrayList<>(allSentenceListDto.stream()
                    .map(sentencesDTO -> sentencesDTO.getBookSentenceId().toString())
                    .toList());

            Collections.shuffle(allSentenceIdList);

            log.info("shuffled list....................."+allSentenceIdList);

            sentenceSortKey = String.join(",",allSentenceIdList);

            log.info("shuffled list str....................."+sentenceSortKey);
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("accessToken", accessToken);
        resultMap.put("refToken", refreshToken);

        if(!sentenceSortKey.equals("")) resultMap.put("sentenceSortKey", sentenceSortKey);

        return new ResponseEntity<>(resultMap, header, HttpStatus.valueOf(201));
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
