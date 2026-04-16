package com.dev.costurartbaby.service;

import com.dev.costurartbaby.entities.dto.ContaRequest;
import com.dev.costurartbaby.entities.dto.ContaResponse;
import com.dev.costurartbaby.entities.dto.ContaUpdate;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ContaService {
    ContaResponse getContaById(UUID id);
    Page<ContaResponse> getContas(int page, int size);
    void createConta(ContaRequest request);
    void updateConta(ContaUpdate contaUpdate, UUID id);
    void deleteConta(UUID id);
    void desativarConta(UUID id);
    void ativarConta(UUID id);
    void bloquearConta(UUID id);
    void desbloquearConta(UUID id);
}
