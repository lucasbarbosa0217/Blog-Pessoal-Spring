package com.generation.blogpessoal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "tb_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O Atributo Nome é Obrigatório!")
    private String name;

    @Schema(example = "email@email.com.br")
    @NotNull(message = "O Atributo email é Obrigatório!")
    @Email(message = "O Atributo email deve ser um email válido!")
    private String email;

    @JsonIgnoreProperties("password")
    @NotBlank(message = "O Atributo password é Obrigatório!")
    @Size(min = 8, message = "O atributo password deve ter no mínimo 8 caracteres")
    private String password;

    @Size(max = 5000, message = "O link da foto não pode ser maior do que 5000 caracteres")
    private String photo;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.REMOVE)
    @JsonIgnoreProperties({"blog", "comment", "user"})
    private List<Blog> blog;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.REMOVE)
    @JsonIgnoreProperties({"bog", "user"})
    private List<Comment> comment;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles")
    @JsonIgnoreProperties("users")
    private Set<Role> roles = new HashSet<Role>();

    public User(Long id, String name, String email, String password, String photo, List<Blog> blog, List<Comment> comment, Set<Role> roles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.photo = photo;
        this.blog = blog;
        this.comment = comment;
        this.roles = roles;
    }

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<Blog> getBlog() {
        return blog;
    }

    public void setBlog(List<Blog> blog) {
        this.blog = blog;
    }

    public List<Comment> getComment() {
        return comment;
    }

    public void setComment(List<Comment> comment) {
        this.comment = comment;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}