package com.generation.blogpessoal.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.generation.blogpessoal.model.Role;
import com.generation.blogpessoal.repository.RoleRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;

    
    @GetMapping
    public ResponseEntity<List<Role>> getAll(){
    	return ResponseEntity.ok(roleRepository.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Role> getById(@PathVariable Long id){
        return roleRepository.findById(id)
                .map(role -> ResponseEntity.ok(role))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
	public ResponseEntity<Role> post (@Valid @RequestBody Role role){
    		if(role.getName().startsWith("ROLE_")) {
    			role.setName(role.getName().substring(5, role.getName().length()));
    		}
    		
    		role.setName("ROLE_"+role.getName().trim().toUpperCase());
			return ResponseEntity.status(HttpStatus.CREATED).body(roleRepository.save(role));
	}
    
	@ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete (@PathVariable Long id) {
		if(roleRepository.findById(id).isPresent()){
			roleRepository.deleteById(id);
		}else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Este role não existe!");
		}
	
	}
}
