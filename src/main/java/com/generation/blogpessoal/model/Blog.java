package com.generation.blogpessoal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tb_blog")
@EntityListeners(AuditingEntityListener.class)

public class Blog {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O atributo títle é Obrigatório!")
    @Size(min = 5, max = 100, message = "O atributo títle deve conter no mínimo 05 e no máximo 100 caracteres")
    private String title;

    @NotBlank(message = "O atributo text é Obrigatório!")
    @Size(min = 10, max = 1000, message = "O atributo text deve conter no mínimo 10 e no máximo 1000 caracteres")
    private String text;

    private String urlpath;
    @CreationTimestamp
    private LocalDateTime createdTimestamp;
    @UpdateTimestamp
    private LocalDateTime updatedTimestamp;

    @ManyToOne
    @JsonIgnoreProperties("blog")
    private Theme theme;

    @ManyToOne
    @JsonIgnoreProperties({"blog", "password", "comment"})
    private User user;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "blog", cascade = CascadeType.REMOVE)
    @JsonIgnoreProperties("blog")
    private List<Comment> comment;

    public String getUrlpath() {
        return urlpath;
    }

    public void setUrlpath(String urlPath) {
        this.urlpath = urlPath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(LocalDateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public LocalDateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(LocalDateTime data) {
        this.updatedTimestamp = data;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Comment> getComment() {
        return comment;
    }

    public void setComment(List<Comment> comment) {
        this.comment = comment;
    }
}
