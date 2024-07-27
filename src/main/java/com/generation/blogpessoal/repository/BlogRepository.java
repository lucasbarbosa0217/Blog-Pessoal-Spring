package com.generation.blogpessoal.repository;

import com.generation.blogpessoal.model.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    List<Blog> findAllByTitleContainingIgnoreCase(@Param("title") String title);

    List<Blog> findAllByTextContainingIgnoreCase(@Param("texto") String text);

    Optional<Blog> findByUrlpath(@Param("urlpath") String urlpath);

}
