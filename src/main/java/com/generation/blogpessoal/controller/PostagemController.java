package com.generation.blogpessoal.controller;

import java.util.ArrayList;
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
import com.generation.blogpessoal.repository.ComentarioRepository;
import com.generation.blogpessoal.repository.PostagemRepository;
import com.generation.blogpessoal.repository.TemaRepository;
import com.generation.blogpessoal.repository.UsuarioRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/postagens")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PostagemController {

	@Autowired
	private PostagemRepository postagemRepository;

	@Autowired
	private TemaRepository temaRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;
	

	@Autowired
	private ComentarioRepository comentarioRepository;

	@GetMapping
	public ResponseEntity<List<Postagem>> getAll() {
		return ResponseEntity.ok(postagemRepository.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Postagem> getById(@PathVariable Long id) {
		return postagemRepository.findById(id).map(resposta -> ResponseEntity.ok(resposta))
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("titulo/{titulo}")
	public ResponseEntity<List<Postagem>> getByTitulo(@PathVariable String titulo) {
		return ResponseEntity.ok(postagemRepository.findAllByTituloContainingIgnoreCase(titulo));
	}

	@GetMapping("texto/{texto}")
	public ResponseEntity<List<Postagem>> getByTexto(@PathVariable String texto) {
		return ResponseEntity.ok(postagemRepository.findAllByTextoContainingIgnoreCase(texto));
	}

	@PostMapping
	public ResponseEntity<Postagem> post(@Valid @RequestBody Postagem postagem) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String username = null;

		if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
			username = ((UserDetails) authentication.getPrincipal()).getUsername();
		}

		Optional<Usuario> usuarioBanco = usuarioRepository.findByUsuario(username);

		Optional<Tema> tema = Optional.ofNullable(postagem.getTema());
		if (tema.isPresent()) {
			if (temaRepository.existsById(postagem.getTema().getId()) && usuarioBanco.isPresent()) {
				postagem.setUsuario(usuarioBanco.get());
				if (postagem.getComentario() == null) {
					postagem.setComentario(new ArrayList<Comentario>());
				}
				return ResponseEntity.status(HttpStatus.CREATED).body(postagemRepository.save(postagem));
			}
		}

		if (tema.isEmpty() && usuarioBanco.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"O usuário e tema não existem!");
		} else if (tema.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O tema não existe!");
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O usuário não existe!");
		}

	}

	@PutMapping
	public ResponseEntity<Postagem> put(@Valid @RequestBody Postagem postagem) {

		Optional<Postagem> postagemBanco = postagemRepository.findById(postagem.getId());

		Optional<Tema> tema = Optional.ofNullable(postagem.getTema());
		if (tema.isPresent()) {
			if (postagemBanco.isPresent()) {
				if (temaRepository.existsById(postagem.getTema().getId())) {
					postagem.setUsuario(postagemBanco.get().getUsuario());
					postagem.setComentario(postagemBanco.get().getComentario());
					return ResponseEntity.status(HttpStatus.OK).body(postagemRepository.save(postagem));
				}else {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O tema não existe!");
				}
			} else {
				return ResponseEntity.notFound().build();
			}
		}
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O tema não existe!");

	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		Optional<Postagem> postagem = postagemRepository.findById(id);

		if (postagem.isEmpty())
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
		if (postagem.get().getUsuario().equals(usuarioBanco.get()) || isAdmin) {
			postagemRepository.deleteById(id);
			return;
		}}

		throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário logado não é o mesmo que fez o post!");
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
	@DeleteMapping("/comentar/{id}")
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
