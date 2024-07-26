package com.generation.blogpessoal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import com.generation.blogpessoal.model.Postagem;

@Repository
public interface PostagemRepository extends JpaRepository<Postagem, Long>{
	public List<Postagem> findAllByTituloContainingIgnoreCase(@Param("titulo") String titulo);

	public List<Postagem> findAllByTextoContainingIgnoreCase(@Param("texto") String texto);
	public Optional<Postagem> findByUrlpath(@Param("urlpath") String urlpath);

}
