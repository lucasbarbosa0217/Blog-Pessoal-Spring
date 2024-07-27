package com.generation.blogpessoal.controller;

import com.generation.blogpessoal.model.Blog;
import com.generation.blogpessoal.model.Theme;
import com.generation.blogpessoal.model.User;
import com.generation.blogpessoal.repository.BlogRepository;
import com.generation.blogpessoal.repository.ThemeRepository;
import com.generation.blogpessoal.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/postagens")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PostagemController {

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private AuthenticationService authenticationService;

    public static String toSlug(String title) {
        String slug = title.toLowerCase();
        slug = Normalizer.normalize(slug, Normalizer.Form.NFD);
        slug = slug.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        slug = slug.replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-").replaceAll("-+", "-");
        slug = slug.replaceAll("^-|-$", "");
        return slug;
    }

    @GetMapping
    public ResponseEntity<List<Blog>> getAll() {
        return ResponseEntity.ok(blogRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Blog> getById(@PathVariable Long id) {
        return blogRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "O post de id " + id + " não existe!"));
    }

    @GetMapping("titulo/{title}")
    public ResponseEntity<List<Blog>> getByTitle(@PathVariable String title) {
        return ResponseEntity.ok(blogRepository.findAllByTitleContainingIgnoreCase(title));
    }

    @GetMapping("texto/{text}")
    public ResponseEntity<List<Blog>> getByText(@PathVariable String text) {
        return ResponseEntity.ok(blogRepository.findAllByTextContainingIgnoreCase(text));
    }

    @GetMapping("urlPath/{urlPath}")
    public ResponseEntity<Blog> getByUrlPath(@PathVariable String urlPath) {
        return blogRepository.findByUrlpath(urlPath)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post não existe."));
    }

    @PostMapping
    public ResponseEntity<Blog> post(@Valid @RequestBody Blog post) {
        Optional<User> loggedUser = authenticationService.getLoggedUser();

        if (loggedUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O usuário logado não existe!");
        }

        String slugUrl = toSlug(post.getTitle());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        if (blogRepository.findByUrlpath(slugUrl).isPresent()) {
            slugUrl = slugUrl + "-" + dateFormat.format(new Date());
            slugUrl = toSlug(slugUrl);
        }

        post.setUrlpath(slugUrl);

        Optional<Theme> theme = Optional.ofNullable(post.getTheme());
        if (theme.isEmpty() || themeRepository.findById(theme.get().getId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O tema não existe!");
        }

        post.setUser(loggedUser.get());
        if (post.getComment() == null) {
            post.setComment(new ArrayList<>());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(blogRepository.save(post));
    }

    @PutMapping
    public ResponseEntity<Blog> put(@Valid @RequestBody Blog blog) {
        Optional<Blog> storedBlog = blogRepository.findById(blog.getId());
        if (storedBlog.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A Postagem não existe");
        }
        Optional<Theme> theme = Optional.ofNullable(blog.getTheme());
        Optional<Theme> storedTheme = Optional.empty();
        if (theme.isPresent()) {
            storedTheme = themeRepository.findById(theme.get().getId());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O Tema não existe na sua requisição!");
        }
        Optional<User> loggedUser = authenticationService.getLoggedUser();
        if (loggedUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário logado não existe!");
        }
        boolean isAdmin = authenticationService.isLoggedUserAdmin();

        if (storedTheme.isPresent()) {
            if (loggedUser.get().getEmail().equals(storedBlog.get().getUser().getEmail()) || isAdmin) {
                blog.setUser(storedBlog.get().getUser());
                blog.setCreatedTimestamp(storedBlog.get().getCreatedTimestamp());
                blog.setComment(storedBlog.get().getComment());
                String slugUrl = toSlug(blog.getTitle());
                if (!slugUrl.equals(storedBlog.get().getUrlpath())) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    if (blogRepository.findByUrlpath(slugUrl).isPresent()) {
                        slugUrl = slugUrl + "-" + dateFormat.format(new Date());
                        slugUrl = toSlug(slugUrl);
                    }
                    blog.setUrlpath(slugUrl);
                }
                blog.setUrlpath(slugUrl);
                return ResponseEntity.status(HttpStatus.OK).body(blogRepository.save(blog));
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
        Optional<Blog> storedBlog = blogRepository.findById(id);

        if (storedBlog.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        Optional<User> loggedUser = authenticationService.getLoggedUser();
        boolean isAdmin = authenticationService.isLoggedUserAdmin();

        if (loggedUser.isPresent()) {
            if (storedBlog.get().getUser().equals(loggedUser.get()) || isAdmin) {
                blogRepository.deleteById(id);
                return;
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário logado não é o mesmo que fez o post!");
    }
}
