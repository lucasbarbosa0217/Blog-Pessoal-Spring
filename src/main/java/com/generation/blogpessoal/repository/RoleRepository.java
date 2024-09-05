package com.generation.blogpessoal.repository;

import com.generation.blogpessoal.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(@Param("name") String name);
}