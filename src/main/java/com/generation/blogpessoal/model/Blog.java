package com.generation.blogpessoal.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Document(collection = "blog")
public class Blog {

    @Id
    private String id;

    @NotBlank(message = "O atributo title é Obrigatório!")
    @Size(min = 5, max = 100, message = "O atributo title deve conter no mínimo 05 e no máximo 100 caracteres")
    private String title;

    @NotBlank(message = "O atributo text é Obrigatório!")
    @Size(min = 10, max = 3000, message = "O atributo text deve conter no mínimo 10 e no máximo 3000 caracteres")
    private String text;

    private String urlPath;

    @CreatedDate
    private LocalDateTime createdTimestamp;

    @LastModifiedDate
    private LocalDateTime updatedTimestamp;

    @DBRef
    private Theme theme;

    @JsonIgnoreProperties("password")
    @DBRef
    private User user;


	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
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

	public void setUpdatedTimestamp(LocalDateTime updatedTimestamp) {
		this.updatedTimestamp = updatedTimestamp;
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

   
    
}
