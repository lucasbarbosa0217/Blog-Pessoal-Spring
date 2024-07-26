package com.generation.blogpessoal.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.generation.blogpessoal.model.Role;
import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.RoleRepository;
import com.generation.blogpessoal.repository.UsuarioRepository;

import jakarta.annotation.PostConstruct;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class AdminCreator {

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Value("${admin.user.password}")
	private String password;

	@Value("${admin.user.email}")
	private String email;
	@PostConstruct
	public void init() {
		// Criar roles se não existirem
		if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
			roleRepository.save(new Role("ROLE_ADMIN"));
		}

		if (roleRepository.findByName("ROLE_USER").isEmpty()) {
			roleRepository.save(new Role("ROLE_USER"));
		}

		// Criar usuário admin se não existir
		if (usuarioRepository.findByUsuario(email).isEmpty()) {
			Usuario admin = new Usuario();
			admin.setId(1L);
			admin.setNome("Admin");
			admin.setUsuario(email);
			admin.setSenha(passwordEncoder.encode(password));

			Set<Role> roles = new HashSet<>();
			roles.add(roleRepository.findByName("ROLE_ADMIN").get());
			roles.add(roleRepository.findByName("ROLE_USER").get());
			admin.setRoles(roles);

			usuarioRepository.save(admin);
		}
	}
}
