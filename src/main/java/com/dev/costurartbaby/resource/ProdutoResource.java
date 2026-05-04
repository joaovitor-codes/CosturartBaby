package com.dev.costurartbaby.resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.costurartbaby.entities.dto.ProdutoRequest;
import com.dev.costurartbaby.entities.dto.ProdutoResponse;
import com.dev.costurartbaby.entities.dto.ProdutoUpdate;
import com.dev.costurartbaby.service.ProdutoService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
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

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProdutoResponse> updateProdutoPut(@PathVariable Long id, @RequestBody @Valid ProdutoRequest request) {
        return ResponseEntity.ok(produtoService.updateProdutoPut(id, request));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProdutoResponse> updateProdutoPatch(@PathVariable Long id, @RequestBody ProdutoUpdate request) {
        return ResponseEntity.ok(produtoService.updateProdutoPatch(id, request));
    }

    @GetMapping
    public ResponseEntity<Page<ProdutoResponse>> getProdutos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(produtoService.getProdutos(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> getProdutoById(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.getProdutoById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduto(@PathVariable Long id) {
        produtoService.deleteProduto(id);
        return ResponseEntity.noContent().build();
    }
    
}
