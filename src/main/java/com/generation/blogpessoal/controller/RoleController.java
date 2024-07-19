package com.generation.blogpessoal.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.model.Role;
import com.generation.blogpessoal.model.Tema;
import com.generation.blogpessoal.repository.RoleRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;


    @PostMapping
	public ResponseEntity<Role> post (@Valid @RequestBody Role role){
			return ResponseEntity.status(HttpStatus.CREATED).body(roleRepository.save(role));
	}
}
