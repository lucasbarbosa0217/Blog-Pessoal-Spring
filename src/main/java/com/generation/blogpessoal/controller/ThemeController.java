package com.generation.blogpessoal.controller;

import com.generation.blogpessoal.model.Blog;
import com.generation.blogpessoal.model.Theme;
import com.generation.blogpessoal.repository.ThemeRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/temas")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ThemeController {

    @Autowired
    private ThemeRepository themeRepository;

    @GetMapping
    public ResponseEntity<List<Theme>> getAll() {
        return ResponseEntity.ok(themeRepository.findAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Theme> getById(@PathVariable Long id) {
        return themeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/descricao/{description}")
    public ResponseEntity<List<Theme>> getByTitle(@PathVariable
                                                  String description) {
        return ResponseEntity.ok(themeRepository
                .findAllByDescriptionContainingIgnoreCase(description));
    }

    @PostMapping
    public ResponseEntity<Theme> post(@Valid @RequestBody Theme theme) {
        theme.setBlog(new ArrayList<Blog>());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(themeRepository.save(theme));
    }

    @PutMapping
    public ResponseEntity<Theme> put(@Valid @RequestBody Theme theme) {
        Optional<Theme> storedTheme = themeRepository.findById(theme.getId());

        if (storedTheme.isPresent()) {
            if (storedTheme.get().getBlog() != null) {
                theme.setBlog(storedTheme.get().getBlog());
            } else {
                theme.setBlog(new ArrayList<Blog>());
            }
            return ResponseEntity.status(HttpStatus.OK).body(themeRepository.save(theme));
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Este tema não existe!");
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        Optional<Theme> theme = themeRepository.findById(id);

        if (theme.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Este tema não existe!");

        themeRepository.deleteById(id);
    }

}