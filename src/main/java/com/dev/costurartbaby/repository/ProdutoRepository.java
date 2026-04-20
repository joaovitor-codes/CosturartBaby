package com.dev.costurartbaby.repository;

import com.dev.costurartbaby.entities.produto.ProdutoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<ProdutoEntity, Long> {
}
