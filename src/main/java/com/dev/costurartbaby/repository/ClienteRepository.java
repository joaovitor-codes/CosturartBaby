package com.dev.costurartbaby.repository;

import com.dev.costurartbaby.entities.cliente.ClienteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository extends JpaRepository<ClienteEntity, UUID> {
    boolean existsByCpf(String cpf);
    Optional<ClienteEntity> findByCpf(String cpf);
    @EntityGraph(attributePaths = {"telefones"})
    Page<ClienteEntity> findAll(Pageable pageable);
}
