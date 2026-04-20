package com.dev.costurartbaby.service.impl;

import com.dev.costurartbaby.config.exception.BusinessException;
import com.dev.costurartbaby.config.exception.ResourceNotFoundException;
import com.dev.costurartbaby.entities.cliente.ClienteEntity;
import com.dev.costurartbaby.entities.cliente.TelefoneEntity;
import com.dev.costurartbaby.entities.dto.ClienteRequest;
import com.dev.costurartbaby.entities.dto.ClienteResponse;
import com.dev.costurartbaby.entities.dto.ClienteUpdate;
import com.dev.costurartbaby.repository.ClienteRepository;
import com.dev.costurartbaby.service.ClienteService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ClienteServiceImpl implements ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional
    public void createCliente(ClienteRequest request) {
        log.info("Criando cliente: {}", request.nome());

        if (clienteRepository.existsByCpf(request.cpf())) {
            log.warn("CPF já cadastrado: {}", request.cpf());
            throw new BusinessException("CPF já cadastrado");
        }

        TelefoneEntity telefone = TelefoneEntity.builder()
                .numero(request.telefone())
                .build();

        ClienteEntity cliente = ClienteEntity.builder()
                .nome(request.nome())
                .cpf(request.cpf())
                .dataNascimento(request.dataNascimento())
                .telefones(telefone != null ? List.of(telefone) : List.of())
                .build();

        if (telefone != null) {
            telefone.setCliente(cliente);
        }

        clienteRepository.save(cliente);
        log.info("Cliente criado: {}", cliente.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse getClienteById(UUID id) {
        var clienteEntity = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        List<String> telefones = clienteEntity.getTelefones().stream()
                .map(TelefoneEntity::getNumero)
                .toList();

        return new ClienteResponse(
                clienteEntity.getId().toString(),
                clienteEntity.getNome(),
                clienteEntity.getCpf(),
                clienteEntity.getDataNascimento().toString(),
                telefones
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse getClienteByCpf(@CPF String cpf) {
        var clienteEntity = clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        List<String> telefones = clienteEntity.getTelefones().stream()
                .map(TelefoneEntity::getNumero)
                .toList();

        return new ClienteResponse(
                clienteEntity.getId().toString(),
                clienteEntity.getNome(),
                clienteEntity.getCpf(),
                clienteEntity.getDataNascimento().toString(),
                telefones
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteResponse> getClientes(int page, int size) {
        return clienteRepository.findAll(PageRequest.of(page, size))
                .map(clienteEntity -> new ClienteResponse(
                        clienteEntity.getId().toString(),
                        clienteEntity.getNome(),
                        clienteEntity.getCpf(),
                        clienteEntity.getDataNascimento().toString(),
                        clienteEntity.getTelefones().stream()
                                .map(TelefoneEntity::getNumero)
                                .toList()
                ));
    }

    @Override
    @Transactional
    public void deleteCliente(UUID id){
        log.info("Deletando cliente com id: {}", id);

        var clienteEntity = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        clienteRepository.delete(clienteEntity);
        log.info("Cliente deletado: {}", id);
    }

    @Override
    @Transactional
    public void updateCliente(UUID id, ClienteUpdate clienteUpdate){
        log.info("Atualizando cliente com id: {}", id);

        var clienteEntity = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        if (!clienteEntity.getConta().isEnabled()){
            throw new BusinessException("Cliente desativado não pode ser atualizado");
        }

        if (!clienteEntity.getConta().isAccountNonLocked()){
            throw new BusinessException("Cliente bloqueado não pode ser atualizado");
        }

        clienteUpdate.nome().ifPresent(clienteEntity::setNome);

        clienteUpdate.cpf().ifPresent(cpf -> {
            if (!clienteEntity.getCpf().equals(cpf) && clienteRepository.existsByCpf(cpf)) {
                throw new BusinessException("CPF já cadastrado");
            }
            clienteEntity.setCpf(cpf);
        });

        clienteUpdate.dataNascimento().ifPresent(clienteEntity::setDataNascimento);

        clienteRepository.save(clienteEntity);
    }

    @Override
    @Transactional
    public void adicionarTelefone(UUID clienteId, String numeroTelefone) {
        log.info("Adicionando telefone para cliente com id: {}", clienteId);

        var clienteEntity = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        TelefoneEntity telefone = TelefoneEntity.builder()
                .numero(numeroTelefone)
                .cliente(clienteEntity)
                .build();

        clienteEntity.getTelefones().add(telefone);
    }

    @Override
    @Transactional
    public void removerTelefone(UUID clienteId, String numeroTelefone) {
        log.info("Removendo telefone para cliente com id: {}", clienteId);

        var clienteEntity = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        var telefoneEntity = clienteEntity.getTelefones().stream()
                .filter(t -> t.getNumero().equals(numeroTelefone))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Telefone não encontrado para este cliente"));

        clienteEntity.getTelefones().remove(telefoneEntity);
        log.info("Telefone removido: {}", telefoneEntity.getId());
    }
}
