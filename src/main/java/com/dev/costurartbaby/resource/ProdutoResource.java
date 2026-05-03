package com.dev.costurartbaby.resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.costurartbaby.entities.dto.ProdutoRequest;
import com.dev.costurartbaby.entities.dto.ProdutoResponse;
import com.dev.costurartbaby.service.ProdutoService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/produtos")
public class ProdutoResource {
    private final ProdutoService produtoService;

    public ProdutoResource(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProdutoResponse> createProduto(@RequestBody @Valid ProdutoRequest request) {
        return ResponseEntity.status(201).body(produtoService.createProduto(request));
    }
    
}
