package com.generation.blogpessoal.controller;

import com.generation.blogpessoal.model.Blog;
import com.generation.blogpessoal.model.Theme;
import com.generation.blogpessoal.model.User;
import com.generation.blogpessoal.repository.BlogRepository;
import com.generation.blogpessoal.repository.ThemeRepository;
import com.generation.blogpessoal.repository.UserRepository;
import com.generation.blogpessoal.service.AuthenticationService;
import com.generation.blogpessoal.service.ImageService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/postagens")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BlogController {

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private AuthenticationService authenticationService;
    
    @Autowired
    private UserRepository userRepository;
    
    private final List<String> allowedFileTypes = List.of("image/jpeg", "image/png", "image/webp", "image/gif");
    
    @Autowired
    private ImageService imageService;

    public static String toSlug(String title) {
        String slug = title.toLowerCase();
        slug = Normalizer.normalize(slug, Normalizer.Form.NFD);
        slug = slug.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        slug = slug.replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-").replaceAll("-+", "-");
        slug = slug.replaceAll("^-|-$", "");
        return slug;
    }

    @GetMapping("/pagina")
    public ResponseEntity<Page<Blog>> getAll(Pageable pageable) {
        return ResponseEntity.ok(blogRepository.findAll(pageable));
    }
    
    @GetMapping
    public ResponseEntity<List<Blog>> getAllnoPage() {
        return ResponseEntity.ok(blogRepository.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Blog> getById(@PathVariable String id) {
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
    
    @GetMapping("tema/{text}")
    public ResponseEntity<List<Blog>> getByTheme(@PathVariable String text) {
    	return themeRepository.findById(text)
                .map((tema) ->  ResponseEntity.ok(blogRepository.findAllByTheme((Theme) tema)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "O tema de id " + text + " não existe!"));
    }
    
    @GetMapping("usuario/{text}")
    public ResponseEntity<List<Blog>> getByUser(@PathVariable String text) {
    	return userRepository.findById(text)
                .map((user) ->  ResponseEntity.ok(blogRepository.findAllByUser((User) user)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "O user de id " + text + " não existe!"));

    }

    @GetMapping("urlPath/{urlPath}")
    public ResponseEntity<Blog> getByUrlPath(@PathVariable String urlPath) {
        return blogRepository.findByUrlPath(urlPath)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post não existe."));
    }

    @PostMapping
    public ResponseEntity<Blog> post(@Valid @RequestBody Blog blog) {
        Optional<User> loggedUser = authenticationService.getLoggedUser();

        if (loggedUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O usuário logado não existe!");
        }

        String slugUrl = toSlug(blog.getTitle());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        if (blogRepository.findByUrlPath(slugUrl).isPresent()) {
            slugUrl = slugUrl + "-" + dateFormat.format(new Date());
            slugUrl = toSlug(slugUrl);
        }

        blog.setUrlPath(slugUrl);

        Optional<Theme> theme = Optional.ofNullable(blog.getTheme());
        if (theme.isEmpty() || themeRepository.findById(theme.get().getId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O tema não existe!");
        }

        blog.setUser(loggedUser.get());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(blogRepository.save(blog));
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

        
        Optional<User> user =  userRepository.findById(storedBlog.get().getUser().getId());        
        
        if(user.isEmpty()) {
        	throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário do post não existe!");
        	}
        
        if (storedTheme.isPresent()) {
            if (loggedUser.get().getEmail().equals(user.get().getEmail()) || isAdmin) {
                blog.setUser(user.get());
                blog.setCreatedTimestamp(storedBlog.get().getCreatedTimestamp());
                String slugUrl = toSlug(blog.getTitle());
                if (!slugUrl.equals(storedBlog.get().getUrlPath())) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    if (blogRepository.findByUrlPath(slugUrl).isPresent()) {
                        slugUrl = slugUrl + "-" + dateFormat.format(new Date());
                        slugUrl = toSlug(slugUrl);
                    }
                    blog.setUrlPath(slugUrl);
                }
                blog.setUrlPath(slugUrl);
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
    public void delete(@PathVariable String id) {
        Optional<Blog> storedBlog = blogRepository.findById(id);

        if (storedBlog.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        Optional<User> loggedUser = authenticationService.getLoggedUser();
        boolean isAdmin = authenticationService.isLoggedUserAdmin();

        Optional<User> user =  userRepository.findById(storedBlog.get().getUser().getId());        

        if(user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário logado não é o mesmo que fez o post!");

        }
        
        
        if (loggedUser.isPresent()) {
           if (user.get().equals(loggedUser.get()) || isAdmin) {
                blogRepository.deleteById(id);
                return;
           }
       }
       throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário logado não é o mesmo que fez o post!");
    }
    
    
    @PostMapping("/foto")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Por favor, selecione uma foto para carregar.");
        }

        String contentType = multipartFile.getContentType();
        if (!allowedFileTypes.contains(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de arquivo não suportado. Apenas JPG, PNG, WEBP e GIF são permitidos.");
        }

        if (multipartFile.getSize() > 10 * 1024 * 1024) { // 10MB
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O tamanho do arquivo excede o limite de 10MB.");
        }

            String url = imageService.upload(multipartFile, "blogImage");
            return ResponseEntity.ok(url);
    
    }

}
