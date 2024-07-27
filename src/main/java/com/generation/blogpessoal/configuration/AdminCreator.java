package com.generation.blogpessoal.configuration;

import com.generation.blogpessoal.model.Role;
import com.generation.blogpessoal.model.User;
import com.generation.blogpessoal.repository.RoleRepository;
import com.generation.blogpessoal.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class AdminCreator {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

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
        if (userRepository.findByEmail(email).isEmpty()) {
            User admin = new User();
            admin.setId(1L);
            admin.setName("Admin");
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(password));

            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName("ROLE_ADMIN").get());
            roles.add(roleRepository.findByName("ROLE_USER").get());
            admin.setRoles(roles);

            userRepository.save(admin);
        }
    }
}
