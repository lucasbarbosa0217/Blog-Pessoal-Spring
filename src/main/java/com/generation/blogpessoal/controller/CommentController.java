package com.generation.blogpessoal.controller;

import com.generation.blogpessoal.model.Comment;
import com.generation.blogpessoal.model.User;
import com.generation.blogpessoal.repository.BlogRepository;
import com.generation.blogpessoal.repository.CommentRepository;
import com.generation.blogpessoal.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/comentario")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CommentController {

    @Autowired
    private BlogRepository blogRepository;


    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/postagem/{id}")
    public ResponseEntity<List<Comment>> pegarComentariosPostagem(@PathVariable Long id) {

        if (blogRepository.existsById(id)) {
            return ResponseEntity.ok(commentRepository.findByBlogId(id));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
        return commentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/comentar")
    public ResponseEntity<Comment> saveComment(@Valid @RequestBody Comment comment) {
        if (comment.getBlog() == null || blogRepository.findById(comment.getBlog().getId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Optional<User> storedUser = authenticationService.getLoggedUser();

        if (storedUser.isPresent()) {
            comment.setUser(storedUser.get());
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário logado não existe no banco de dados!");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(commentRepository.save(comment));
    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id) {
        Optional<Comment> comment = commentRepository.findById(id);

        if (comment.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        Optional<User> storedUser = authenticationService.getLoggedUser();

        if (storedUser.isPresent()) {
            if (comment.get().getUser().equals(storedUser.get()) || authenticationService.isLoggedUserAdmin()) {
                commentRepository.deleteById(id);
                return;
            }
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário logado não é o mesmo que fez o comentário!");
    }
}
