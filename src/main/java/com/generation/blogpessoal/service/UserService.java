package com.generation.blogpessoal.service;

import com.generation.blogpessoal.model.User;
import com.generation.blogpessoal.model.UserLogin;
import com.generation.blogpessoal.repository.UserRepository;
import com.generation.blogpessoal.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public Optional<User> saveUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent())
            return Optional.empty();
        user.setPassword(encryptUser(user.getPassword()));
        return Optional.of(userRepository.save(user));
    }

    public Optional<User> updateUser(User user) {
        if (userRepository.findById(user.getId()).isPresent()) {
            Optional<User> buscaUsuario = userRepository.findByEmail(user.getEmail());
            if ((buscaUsuario.isPresent()) && (!Objects.equals(buscaUsuario.get().getId(), user.getId())))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este email já foi cadastrado!", null);
            if(user.getPassword() != null && !user.getPassword().isEmpty()) {
                user.setPassword(encryptUser(user.getPassword()));

            }else {
                user.setPassword(encryptUser(buscaUsuario.get().getPassword()));
            }
            
            
            return Optional.of(userRepository.save(user));
        }
        return Optional.empty();
    }

    public Optional<UserLogin> authenticateUser(Optional<UserLogin> usuarioLogin) {

        assert usuarioLogin.isPresent();
        var credenciais = new UsernamePasswordAuthenticationToken(usuarioLogin.get().getEmail(), usuarioLogin.get().getPassword());
        Authentication authentication = authenticationManager.authenticate(credenciais);
        if (authentication.isAuthenticated()) {
            Optional<User> usuario = userRepository.findByEmail(usuarioLogin.get().getEmail());
            if (usuario.isPresent()) {
                usuarioLogin.get().setId(usuario.get().getId());
                usuarioLogin.get().setName(usuario.get().getName());
                usuarioLogin.get().setPhoto(usuario.get().getPhoto());
                usuarioLogin.get().setToken(generateToken(usuarioLogin.get().getEmail()));
                usuarioLogin.get().setPassword("");
                usuarioLogin.get().setRole(usuario.get().getRoles());
                return usuarioLogin;
            }
        }
        return Optional.empty();
    }

    private String encryptUser(String senha) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(senha);
    }

    private String generateToken(String usuario) {
        return "Bearer " + jwtService.generateToken(usuario);
    }

}