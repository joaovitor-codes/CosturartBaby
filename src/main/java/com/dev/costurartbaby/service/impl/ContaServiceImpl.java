package com.dev.costurartbaby.service.impl;

import com.dev.costurartbaby.entities.cliente.ContaEntity;
import com.dev.costurartbaby.entities.cliente.ContaRole;
import com.dev.costurartbaby.entities.dto.ContaRequest;
import com.dev.costurartbaby.entities.dto.ContaResponse;
import com.dev.costurartbaby.entities.dto.ContaUpdate;
import com.dev.costurartbaby.config.exception.BusinessException;
import com.dev.costurartbaby.config.exception.ResourceNotFoundException;
import com.dev.costurartbaby.repository.ContaRepository;
import com.dev.costurartbaby.service.ContaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementação do serviço de gerenciamento de contas.
 * Esta classe é responsável por realizar as operações de CRUD (Create, Read, Update, Delete) para as contas de usuário.
 * Ela utiliza o repositório de contas para acessar o banco de dados e o PasswordEncoder para garantir a segurança das senhas.
 */


@Service
@Slf4j
public class ContaServiceImpl implements ContaService {
    private final ContaRepository contaRepository;
    private final PasswordEncoder passwordEncoder;

    public ContaServiceImpl(ContaRepository contaRepository, PasswordEncoder passwordEncoder) {
        this.contaRepository = contaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public ContaResponse getContaById(UUID id) {
        var contaEntity = contaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada"));

        return new ContaResponse(contaEntity.getId(), contaEntity.getLogin());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContaResponse> getContas(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ContaEntity> contas = contaRepository.findAll(pageable);
        return contas.map(ContaResponse::new);
    }

    @Override
    @Transactional
    public void createConta(ContaRequest request){
        log.info("Criando conta: {}", request.login());

        if (contaRepository.existsByLogin(request.login())) {
            log.warn("Email {} já está em uso", request.login());
            throw new BusinessException("Email já está em uso");
        }

        ContaEntity conta = ContaEntity.builder()
                .login(request.login())
                .senha(passwordEncoder.encode(request.senha()))
                .enabled(true)
                .role(ContaRole.USER)
                .build();

        contaRepository.save(conta);
        log.info("Conta {} criada", conta.getId());
    }

    @Override
    @Transactional
    public void updateConta(ContaUpdate contaUpdate, UUID id){
        log.info("Atualizando conta com id: {}", id);

        var contaEntity = contaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada"));

        if (!contaEntity.isEnabled()) {
            throw new BusinessException("Conta desativada não pode ser atualizada");
        }

        if (!contaEntity.isAccountNonLocked()) {
            throw new BusinessException("Conta bloqueada não pode ser atualizada");
        }

        if (contaUpdate.login().isPresent()) {
            String novoLogin = contaUpdate.login().get();

            if (!contaEntity.getLogin().equals(novoLogin) &&
                    contaRepository.existsByLogin(novoLogin)) {
                throw new BusinessException("Email já está em uso");
            }

            contaEntity.setLogin(novoLogin);
        }

        contaUpdate.senha().ifPresent(senha ->
                contaEntity.setSenha(passwordEncoder.encode(senha))
        );

        contaRepository.save(contaEntity);
        log.info("Conta {} atualizada", id);
    }

    @Override
    @Transactional
    public void deleteConta(UUID id){
        var contaEntity = contaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada"));
        log.info("Deletando conta com login: {}", contaEntity.getLogin());
        contaRepository.delete(contaEntity);
        log.info("Conta {} deletada", id);
    }

    @Override
    @Transactional
    public void desativarConta(UUID id){
        var contaEntity = contaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada"));
        log.info("Desativando conta com login: {}", contaEntity.getLogin());

        contaEntity.setEnabled(false);
        contaRepository.save(contaEntity);
        log.info("Conta {} desativada", id);
    }

    @Override
    @Transactional
    public void ativarConta(UUID id){
        var contaEntity = contaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada"));
        log.info("Ativando conta com login: {}", contaEntity.getLogin());

        contaEntity.setEnabled(true);
        contaRepository.save(contaEntity);
        log.info("Conta {} ativada", id);
    }

    @Override
    @Transactional
    public void bloquearConta(UUID id){
        var contaEntity = contaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada"));
        log.info("Bloqueando conta com login: {}", contaEntity.getLogin());

        contaEntity.setLocked(true);
        contaRepository.save(contaEntity);
        log.info("Conta {} bloqueada", id);
    }

    @Override
    @Transactional
    public void desbloquearConta(UUID id){
        var contaEntity = contaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada"));
        log.info("Desbloqueando conta com login: {}", contaEntity.getLogin());

        contaEntity.setLocked(false);
        contaRepository.save(contaEntity);
        log.info("Conta {} desbloqueada", id);
    }
}
