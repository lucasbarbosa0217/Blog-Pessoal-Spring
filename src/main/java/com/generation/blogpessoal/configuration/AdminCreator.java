package com.generation.blogpessoal.configuration;

import org.springframework.beans.factory.annotation.Autowired;
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
		if (usuarioRepository.findByUsuario("admin@email.com").isEmpty()) {
			Usuario admin = new Usuario();
			admin.setId(1l);
			admin.setNome("Admin");
			admin.setUsuario("admin@email.com");
			admin.setSenha(passwordEncoder.encode("admin123"));

			Set<Role> roles = new HashSet<>();
			roles.add(roleRepository.findByName("ROLE_ADMIN").get());
			roles.add(roleRepository.findByName("ROLE_USER").get());
			admin.setRoles(roles);

			usuarioRepository.save(admin);
		}
	}
}
