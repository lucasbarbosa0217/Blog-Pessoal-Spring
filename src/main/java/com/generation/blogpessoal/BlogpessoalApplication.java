package com.generation.blogpessoal;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "com.generation.blogpessoal.repository")
public class BlogpessoalApplication {

	  @PostConstruct
	    public void init() {
	        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	    }
	
    public static void main(String[] args) {
        SpringApplication.run(BlogpessoalApplication.class, args);

    }

}
