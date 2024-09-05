package com.generation.blogpessoal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "com.generation.blogpessoal.repository")
public class BlogpessoalApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogpessoalApplication.class, args);

    }

}
