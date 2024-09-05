package com.generation.blogpessoal.repository;

import com.generation.blogpessoal.model.Blog;
import com.generation.blogpessoal.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findAllByBlog(Blog blog);
}
