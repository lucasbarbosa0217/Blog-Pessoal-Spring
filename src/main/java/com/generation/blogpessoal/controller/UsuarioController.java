package com.generation.blogpessoal.controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.generation.blogpessoal.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.UsuarioLogin;

import com.generation.blogpessoal.model.Role;
import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.RoleRepository;
import com.generation.blogpessoal.repository.UsuarioRepository;

import com.generation.blogpessoal.service.ImageService;
import com.generation.blogpessoal.service.UsuarioService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private ImageService imageService;

	@Autowired
	private AuthenticationService authenticationService;

	@GetMapping("/all")
	public ResponseEntity<List<Usuario>> getAll() {

		return ResponseEntity.ok(usuarioRepository.findAll());

	}

	@GetMapping("/{id}")
	public ResponseEntity<Usuario> getById(@PathVariable Long id) {
		return usuarioRepository.findById(id).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping("/logar")
	public ResponseEntity<UsuarioLogin> autenticarUsuario(@RequestBody Optional<UsuarioLogin> usuarioLogin) {
		return usuarioService.autenticarUsuario(usuarioLogin)
				.map(resposta -> ResponseEntity.status(HttpStatus.OK).body(resposta))
				.orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
	}

	@PostMapping("/cadastrar")
	public ResponseEntity<Usuario> postUsuario(@RequestBody @Valid Usuario usuario) {
		Optional<Set<Role>> roles = Optional.ofNullable(usuario.getRoles());

		if (roles.isPresent()) {
			usuario.setRoles(null);
			if(roleRepository.findByName("ROLE_USER").isPresent()){
				usuario.setRoles(new HashSet<Role>(List.of(roleRepository.findByName("ROLE_USER").get())));
			}
		}
		return usuarioService.cadastrarUsuario(usuario)
				.map(resposta -> ResponseEntity.status(HttpStatus.CREATED).body(resposta))
				.orElse(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
	}

    @PostMapping("/cadastrar/foto")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile multipartFile) {

		Optional<Usuario> usuarioBanco = authenticationService.getLoggedUser();
		if(usuarioBanco.isPresent()) {
			String url = imageService.upload(multipartFile);
			usuarioBanco.get().setFoto(url);
			usuarioRepository.save(usuarioBanco.get());
			return ResponseEntity.ok(url);
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário logado não encontrado no Banco de Dados.");
    }
	
	@PutMapping("/atualizar")
	public ResponseEntity<Usuario> putUsuario(@Valid @RequestBody Usuario usuario) {
		Optional<Usuario> usuarioBanco = authenticationService.getLoggedUser();

		if (usuarioBanco.isPresent()) {
			if (usuarioBanco.get().getUsuario().equals(usuario.getUsuario()) || authenticationService.isLoggedUserAdmin()) {
				return usuarioService.atualizarUsuario(usuario)
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