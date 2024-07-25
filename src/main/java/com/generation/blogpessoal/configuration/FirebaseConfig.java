package com.generation.blogpessoal.configuration;

import com.google.auth.oauth2.GoogleCredentials;


import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
    	
    	
    	 if (FirebaseApp.getApps().isEmpty()) {
    		    FileInputStream serviceAccount = new FileInputStream("src/main/resources/blog-a5aab-firebase-adminsdk-iq4o2-9be4500547.json");
    	        FirebaseOptions options = FirebaseOptions.builder()
    	                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
    	                .setStorageBucket("blog-a5aab.appspot.com") 
    	                .build();
    	        return FirebaseApp.initializeApp(options);
         } else {
             return FirebaseApp.getInstance();
         }
    
    }
}
