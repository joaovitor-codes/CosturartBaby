package com.dev.costurartbaby.repository;

import com.dev.costurartbaby.entities.produto.ProdutoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<ProdutoEntity, Long> {
        Optional<ProdutoEntity> findByYampiProductId(Long yampiProductId);
        Optional<ProdutoEntity> findByYampiSkuId(Long yampiSkuId);
}
