package com.dev.costurartbaby.resource;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.costurartbaby.entities.dto.CategoriaRequest;
import com.dev.costurartbaby.entities.dto.CategoriaResponse;
import com.dev.costurartbaby.service.CategoriaService;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaResource {
    private final CategoriaService categoriaService;

    public CategoriaResource(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> getAllCategorias() {
        return ResponseEntity.ok(categoriaService.getAllCategorias());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> getCategoriaById(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.getCategoriaById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> criarCategoria(@RequestBody @Valid CategoriaRequest categoria) {
        categoriaService.criarCategoria(categoria);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> atualizarCategoria(@PathVariable Long id, @RequestBody @Valid CategoriaRequest categoria) {
        categoriaService.atualizarCategoria(id, categoria);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> excluirCategoria(@PathVariable Long id) {
        categoriaService.deletarCategoria(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/produto/{produtoId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> adicionarProduto(@PathVariable Long id, @PathVariable Long produtoId) {
        categoriaService.adicionarProduto(id, produtoId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/produto/{produtoId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> removerProduto(@PathVariable Long id, @PathVariable Long produtoId) {
        categoriaService.removerProduto(id, produtoId);
        return ResponseEntity.ok().build();
    }

}

