package com.generation.blogpessoal.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

@Document(collection = "user")
public class User {

	@Id
	private String id;

	private String name;

	@NotBlank(message = "O atributo email é obrigatório!")
	private String email;

	@NotBlank(message = "O atributo password é obrigatório!")
	private String password;

	@DBRef
	private Set<Role> roles;
	
	private String photo;


	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}


	public User(String id, String name, String email, String password, String photo, Set<Role> roles) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.photo = photo;

		this.roles = roles;
	}

	public User() {
	}

}