package com.generation.blogpessoal.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotNull;


@Document(collection = "theme")
public class Theme {

    @Id
    private String id;

    @NotNull(message = "O atributo description é obrigatório")
    private String description;



	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


}
