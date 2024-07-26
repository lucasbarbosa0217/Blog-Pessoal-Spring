package com.generation.blogpessoal.configuration;

import com.google.auth.oauth2.GoogleCredentials;


import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {
	@Value("${firebase.secret}")
	private String connectionString;
    @Bean
    public FirebaseApp firebaseApp() throws IOException {
    	
    	
    	 if (FirebaseApp.getApps().isEmpty()) {
    		   // FileInputStream serviceAccount = new FileInputStream("src/main/resources/blog-a5aab-firebase-adminsdk-iq4o2-9be4500547.json");
    	        InputStream stream = new ByteArrayInputStream(connectionString.getBytes(StandardCharsets.UTF_8));
				FirebaseOptions options = FirebaseOptions.builder()
    	                .setCredentials(GoogleCredentials.fromStream(stream))
    	                .setStorageBucket("blog-a5aab.appspot.com") 
    	                .build();
    	        return FirebaseApp.initializeApp(options);
         } else {
             return FirebaseApp.getInstance();
         }
    
    }
}
