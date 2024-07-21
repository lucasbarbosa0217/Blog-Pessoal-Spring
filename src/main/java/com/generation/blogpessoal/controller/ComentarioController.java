package com.generation.blogpessoal.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.generation.blogpessoal.model.Comentario;
import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.ComentarioRepository;
import com.generation.blogpessoal.repository.PostagemRepository;
import com.generation.blogpessoal.repository.UsuarioRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/comentario")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ComentarioController {
	
	@Autowired
	private PostagemRepository postagemRepository;


	@Autowired
	private UsuarioRepository usuarioRepository;
	

	@Autowired
	private ComentarioRepository comentarioRepository;
	
	@GetMapping("/postagem/{id}")
	public ResponseEntity<List<Comentario>> pegarComentariosPostagem(@PathVariable Long  id){
		
		 if(postagemRepository.existsById(id)) {
			 return ResponseEntity.ok(comentarioRepository.findByPostagemId(id));
		 }
		 
		 return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		
	} 
	
	
	@GetMapping("/{id}")
	public ResponseEntity<Comentario> comentarioPorId(@PathVariable Long  id){
		return comentarioRepository.findById(id)
				.map(response -> ResponseEntity.ok(response))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	} 
	
	
	@PostMapping("/comentar")
	public ResponseEntity<Comentario> comentar(@Valid @RequestBody Comentario comentario){
		if(comentario.getPostagem() == null || postagemRepository.findById(comentario.getPostagem().getId()).isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND); 
		}
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = ((UserDetails) authentication.getPrincipal()).getUsername();
		Optional<Usuario> usuarioBd = usuarioRepository.findByUsuario(username);

		if(usuarioBd.isPresent()) {
			comentario.setUsuario(usuarioBd.get());
		}else {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não existe no banco de dados!");
		}
		
		return ResponseEntity.status(HttpStatus.CREATED).body(comentarioRepository.save(comentario));
	}
	
	
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{id}")
	public void deleteComentario(@PathVariable Long id) {
		Optional<Comentario> comentario = comentarioRepository.findById(id);

		if (comentario.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String username = null;
		boolean isAdmin = false;

		if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
			username = ((UserDetails) authentication.getPrincipal()).getUsername();
			isAdmin = ((UserDetails) authentication.getPrincipal()).getAuthorities().stream()
					.anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));
		}

		Optional<Usuario> usuarioBanco = usuarioRepository.findByUsuario(username);

		if(usuarioBanco.isPresent()) {
		if (comentario.get().getUsuario().equals(usuarioBanco.get()) || isAdmin) {
			comentarioRepository.deleteById(id);
			return;
		}}

		throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário logado não é o mesmo que fez o comentário!");
	}
	
	
}
