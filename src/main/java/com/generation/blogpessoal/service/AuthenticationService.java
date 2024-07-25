package com.generation.blogpessoal.service;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    UsuarioRepository usuarioRepository;
    public Optional<Usuario> getLoggedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            username = ((UserDetails) authentication.getPrincipal()).getUsername();
        }

        return usuarioRepository.findByUsuario(username);
    }

    public boolean isLoggedUserAdmin(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = false;

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            isAdmin = ((UserDetails) authentication.getPrincipal()).getAuthorities().stream()
                    .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));
        }

        return isAdmin;
    }
}