package com.dev.costurartbaby.repository;

import com.dev.costurartbaby.entities.cliente.EnderecoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EnderecoRepository extends JpaRepository<EnderecoEntity, UUID> {
}
