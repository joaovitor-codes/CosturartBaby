package com.dev.costurartbaby.repository;

import com.dev.costurartbaby.entities.cliente.TelefoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TelefoneRepository extends JpaRepository<TelefoneEntity, UUID> {
}
