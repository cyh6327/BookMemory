package com.yh.bookMemory;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

//@Configuration
//public class FirebaseConfig {
//    @Bean
//    public FirebaseApp firebaseApp() throws IOException {
//        FileInputStream serviceAccount = new FileInputStream("book-memory-9263b-firebase-adminsdk-f0pq2-c8a2654506.json");
//
//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                .build();
//
//        return FirebaseApp.initializeApp(options);
//    }
//}
