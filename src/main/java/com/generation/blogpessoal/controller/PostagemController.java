package com.generation.blogpessoal.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.generation.blogpessoal.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Comentario;
import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.model.Tema;
import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.PostagemRepository;
import com.generation.blogpessoal.repository.TemaRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/postagens")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PostagemController {

    @Autowired
    private PostagemRepository postRepository;

    @Autowired
    private TemaRepository themeRepository;

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping
    public ResponseEntity<List<Postagem>> getAll() {
        return ResponseEntity.ok(postRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Postagem> getById(@PathVariable Long id) {
        return postRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "O post de id "+ id+ " não existe!"));
    }

    @GetMapping("titulo/{title}")
    public ResponseEntity<List<Postagem>> getByTitle(@PathVariable String title) {
        return ResponseEntity.ok(postRepository.findAllByTituloContainingIgnoreCase(title));
    }

    @GetMapping("texto/{text}")
    public ResponseEntity<List<Postagem>> getByText(@PathVariable String text) {
        return ResponseEntity.ok(postRepository.findAllByTextoContainingIgnoreCase(text));
    }

    @GetMapping("urlPath/{urlPath}")
    public ResponseEntity<List<Postagem>> getByUrlPath(@PathVariable String urlPath) {
        return ResponseEntity.ok(postRepository.findAllByUrlPathContainingIgnoreCase(urlPath));
    }

    @PostMapping
    public ResponseEntity<Postagem> post(@Valid @RequestBody Postagem post) {
        Optional<Usuario> loggedUser = authenticationService.getLoggedUser();

        if (loggedUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O usuário logado não existe!");
        }

        try {
            String encodedString = URLEncoder.encode(post.getTitulo(), StandardCharsets.UTF_8.toString());
            post.setUrlPath(encodedString);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        Optional<Tema> theme = Optional.ofNullable(post.getTema());
        if (theme.isEmpty() || themeRepository.findById(theme.get().getId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O tema não existe!");
        }

        post.setUsuario(loggedUser.get());
        if (post.getComentario() == null) {
            post.setComentario(new ArrayList<Comentario>());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(postRepository.save(post));
    }

    @PutMapping
    public ResponseEntity<Postagem> put(@Valid @RequestBody Postagem postagem) {
        Optional<Postagem> storedPost = postRepository.findById(postagem.getId());
        if (storedPost.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A Postagem não existe");
        }
        Optional<Tema> theme = Optional.ofNullable(postagem.getTema());
        Optional<Tema> storedTheme = Optional.empty();
        if (theme.isPresent()) {
            storedTheme = themeRepository.findById(theme.get().getId());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O Tema não existe na sua requisição!");
        }
        Optional<Usuario> loggedUser = authenticationService.getLoggedUser();
        if (loggedUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário logado não existe!");
        }
        boolean isAdmin = authenticationService.isLoggedUserAdmin();

        if (storedTheme.isPresent()) {
            if (loggedUser.get().getUsuario().equals(storedPost.get().getUsuario().getUsuario()) || isAdmin) {
                if (!isAdmin) {
                    postagem.setUsuario(storedPost.get().getUsuario());
                }
                postagem.setComentario(storedPost.get().getComentario());
                return ResponseEntity.status(HttpStatus.OK).body(postRepository.save(postagem));
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário logado não foi que fez o post!");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O Tema não existe no banco de dados!");
        }
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        Optional<Postagem> storedPost = postRepository.findById(id);

        if (storedPost.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        Optional<Usuario> loggedUser = authenticationService.getLoggedUser();
        boolean isAdmin = authenticationService.isLoggedUserAdmin();

        if (loggedUser.isPresent()) {
            if (storedPost.get().getUsuario().equals(loggedUser.get()) || isAdmin) {
                postRepository.deleteById(id);
                return;
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário logado não é o mesmo que fez o post!");
    }
}
