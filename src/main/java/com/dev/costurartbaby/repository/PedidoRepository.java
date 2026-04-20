package com.dev.costurartbaby.repository;

import com.dev.costurartbaby.entities.produto.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PedidoRepository extends JpaRepository<PedidoEntity, UUID> {
}
