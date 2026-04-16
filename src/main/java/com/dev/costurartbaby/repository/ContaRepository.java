package com.dev.costurartbaby.repository;

import com.dev.costurartbaby.entities.ContaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface ContaRepository extends JpaRepository<ContaEntity, UUID> {
    UserDetails findByLogin(String login);
    Boolean existsByLogin(String login);
}
