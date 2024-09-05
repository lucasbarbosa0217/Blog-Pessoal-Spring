package com.generation.blogpessoal.repository;

import com.generation.blogpessoal.model.Theme;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ThemeRepository extends MongoRepository<Theme, String> {
    List<Theme> findAllByDescriptionContainingIgnoreCase(@Param("description") String description);

}