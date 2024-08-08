package com.generation.blogpessoal.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class BasicSecurityConfig {

    @Autowired
    private JwtAuthFilter authFilter;

    @Bean
    UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .sessionManagement(management -> management
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .cors(withDefaults());

        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/usuarios/logar").permitAll()
                        .requestMatchers("/usuarios/cadastrar").permitAll()
                        .requestMatchers("/usuarios/all").permitAll()
                        .requestMatchers("/error/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/temas/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/temas").permitAll()
                        .requestMatchers(HttpMethod.GET, "/temas/descricao/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/postagens/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/postagens/urlPath/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/postagens/texto/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/postagens/titulo/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/postagens").permitAll()
                        .requestMatchers(HttpMethod.GET, "/comentario/postagem/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS).permitAll()
                        .requestMatchers(HttpMethod.POST, "/roles").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(withDefaults());
        //    .requestMatchers(HttpMethod.POST, "/temas").hasRole("ADMIN")
        //    .requestMatchers(HttpMethod.DELETE, "/temas/**").hasRole("ADMIN")
        //     .requestMatchers(HttpMethod.PUT, "/temas/**").hasRole("ADMIN")
        // .requestMatchers(HttpMethod.POST, "/postagens").hasRole("ADMIN")
        // .requestMatchers(HttpMethod.PUT, "/postagens").hasRole("ADMIN")
        //  .requestMatchers(HttpMethod.DELETE, "/postagens/**").hasRole("ADMIN")
        return http.build();
    }

}