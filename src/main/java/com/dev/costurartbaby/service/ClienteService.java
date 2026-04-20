package com.dev.costurartbaby.service;

import com.dev.costurartbaby.entities.dto.ClienteRequest;
import com.dev.costurartbaby.entities.dto.ClienteResponse;
import com.dev.costurartbaby.entities.dto.ClienteUpdate;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ClienteService {
    void createCliente(ClienteRequest request);
    ClienteResponse getClienteById(UUID id);
    ClienteResponse getClienteByCpf(@CPF String cpf);
    Page<ClienteResponse> getClientes(int page, int size);
    void deleteCliente(UUID id);
    void updateCliente(UUID id, ClienteUpdate clienteUpdate);
    void adicionarTelefone(UUID clienteId, String numeroTelefone);
    void removerTelefone(UUID clienteId, String numeroTelefone);
}
