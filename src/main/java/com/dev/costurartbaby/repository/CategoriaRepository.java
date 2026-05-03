package com.dev.costurartbaby.repository;

import com.dev.costurartbaby.entities.produto.CategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<CategoriaEntity, Long> {
    
}
