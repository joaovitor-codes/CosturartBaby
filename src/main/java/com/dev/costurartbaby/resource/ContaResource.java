package com.dev.costurartbaby.resource;

import com.dev.costurartbaby.entities.dto.ContaRequest;
import com.dev.costurartbaby.entities.dto.ContaResponse;
import com.dev.costurartbaby.entities.dto.ContaUpdate;
import com.dev.costurartbaby.service.ContaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/*
 * A classe ContaResource é um controlador REST responsável por expor endpoints para gerenciar contas de usuários.
 * Ela utiliza a anotação @RestController para indicar que é um controlador REST e @RequestMapping para definir a rota base "/api/contas".
 * Os métodos dentro da classe são anotados com @GetMapping, @PostMapping, @DeleteMapping, @PatchMapping e @PutMapping para mapear as requisições HTTP correspondentes.
 * A segurança dos endpoints é controlada usando a anotação @PreAuthorize, permitindo acesso apenas a usuários com as permissões adequadas (ADMIN ou USER).
 * O serviço ContaService é injetado na classe para realizar as operações de negócio relacionadas às contas.
 */

@RestController
@RequestMapping("/api/contas")
public class ContaResource {
    private final ContaService contaService;

    public ContaResource(ContaService contaService) {
        this.contaService = contaService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ContaResponse>> getContas(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        Page<ContaResponse> contas = contaService.getContas(page, size);
        return ResponseEntity.ok(contas);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createConta(@RequestBody @Valid ContaRequest request){
        contaService.createConta(request);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @securityService.isContaOwner(authentication, #id))")
    public ResponseEntity<ContaResponse> getContaById(@PathVariable UUID id){
        ContaResponse conta = contaService.getContaById(id);
        return ResponseEntity.ok(conta);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteConta(@PathVariable UUID id){
        contaService.deleteConta(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desativar")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @securityService.isContaOwner(authentication, #id))")
    public ResponseEntity<Void> desativarConta(@PathVariable UUID id){
        contaService.desativarConta(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativar")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @securityService.isContaOwner(authentication, #id))")
    public ResponseEntity<Void> ativarConta(@PathVariable UUID id){
        contaService.ativarConta(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/bloquear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> bloquearConta(@PathVariable UUID id){
        contaService.bloquearConta(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desbloquear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> desbloquearConta(@PathVariable UUID id){
        contaService.desbloquearConta(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @securityService.isContaOwner(authentication, #id))")
    public ResponseEntity<Void> updateConta(@PathVariable UUID id, @RequestBody @Valid ContaUpdate request){
        contaService.updateConta(request, id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @securityService.isContaOwner(authentication, #id))")
    public ResponseEntity<Void> partialUpdateConta(@PathVariable UUID id, @RequestBody ContaUpdate request){
        contaService.updateConta(request, id);
        return ResponseEntity.noContent().build();
    }
}
