package com.generation.blogpessoal.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Document(collection = "comment")
public class Comment {

    @Id
    private String id;

    @NotBlank(message = "O atributo text é obrigatório!")
    private String text;

    @LastModifiedDate
    private LocalDateTime updatedTimestamp;
    
    @CreatedDate
    private LocalDateTime createdTimestamp;

    public LocalDateTime getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(LocalDateTime createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	@NotNull(message = "O comentario precisa de um blog")
	@DBRef
	@JsonIgnoreProperties({"text", "user"})
    private Blog blog;

    @JsonIgnoreProperties("password")
    @DBRef
    private User user;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public LocalDateTime getUpdatedTimestamp() {
		return updatedTimestamp;
	}

	public void setUpdatedTimestamp(LocalDateTime updatedTimestamp) {
		this.updatedTimestamp = updatedTimestamp;
	}

	public Blog getBlog() {
		return blog;
	}

	public void setBlog(Blog blog) {
		this.blog = blog;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

   
}

