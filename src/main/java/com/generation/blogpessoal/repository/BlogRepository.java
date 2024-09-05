package com.generation.blogpessoal.repository;

import com.generation.blogpessoal.model.Blog;
import com.generation.blogpessoal.model.Theme;
import com.generation.blogpessoal.model.User;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogRepository extends MongoRepository<Blog, String> {
    List<Blog> findAllByTitleContainingIgnoreCase(@Param("title") String title);

    List<Blog> findAllByTextContainingIgnoreCase(@Param("texto") String text);
    
    List<Blog> findAllByTheme(@Param("theme") Theme theme);
    
    List<Blog> findAllByUser(@Param("user") User user);


 
    Optional<Blog> findByUrlPath(@Param("urlPath") String urlpath);

}
