package com.dev.costurartbaby.repository;

import com.dev.costurartbaby.entities.produto.ProdutoItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoItemRepository extends JpaRepository<ProdutoItemEntity, Long> {
}
