package com.generation.blogpessoal.controller;

import com.generation.blogpessoal.model.Role;
import com.generation.blogpessoal.model.User;
import com.generation.blogpessoal.model.UserLogin;
import com.generation.blogpessoal.repository.RoleRepository;
import com.generation.blogpessoal.repository.UserRepository;
import com.generation.blogpessoal.service.AuthenticationService;
import com.generation.blogpessoal.service.ImageService;
import com.generation.blogpessoal.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UsuarioController {

    private final List<String> allowedFileTypes = List.of("image/jpeg", "image/png", "image/webp", "image/gif");
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ImageService imageService;
    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/is-admin")
    public ResponseEntity<Boolean> isAdmin() {
        return ResponseEntity.ok(authenticationService.isLoggedUserAdmin());
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAll() {

        return ResponseEntity.ok(userRepository.findAll());

    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return userRepository.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/logar")
    public ResponseEntity<UserLogin> authenticateUser(@RequestBody Optional<UserLogin> userLogin) {
        return userService.authenticateUser(userLogin)
                .map(resposta -> ResponseEntity.status(HttpStatus.OK).body(resposta))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<User> postUser(@RequestBody @Valid User user) {
        Optional<Set<Role>> roles = Optional.ofNullable(user.getRoles());

        if (roles.isPresent()) {
            user.setRoles(null);
            if (roleRepository.findByName("ROLE_USER").isPresent()) {
                user.setRoles(new HashSet<>(List.of(roleRepository.findByName("ROLE_USER").get())));
            }
        }
        return userService.saveUser(user)
                .map(resposta -> ResponseEntity.status(HttpStatus.CREATED).body(resposta))
                .orElse(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @PostMapping("/cadastrar/foto")
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

        Optional<User> storedUser = authenticationService.getLoggedUser();
        if (storedUser.isPresent()) {
            String url = imageService.upload(multipartFile);
            storedUser.get().setPhoto(url);
            userRepository.save(storedUser.get());
            return ResponseEntity.ok(url);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário logado não encontrado no Banco de Dados.");
    }

    @PutMapping("/atualizar")
    public ResponseEntity<User> putUsuario(@Valid @RequestBody User user) {
        Optional<User> storedUser = authenticationService.getLoggedUser();

        if (storedUser.isPresent()) {
            if (storedUser.get().getEmail().equals(user.getEmail()) || authenticationService.isLoggedUserAdmin()) {
                return userService.updateUser(user)
                        .map(resposta -> ResponseEntity.status(HttpStatus.OK).body(resposta))
                        .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "O usuário logado não é o mesmo que está sendo editado!");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}