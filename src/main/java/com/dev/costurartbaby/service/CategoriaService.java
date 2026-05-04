package com.dev.costurartbaby.service;


import java.util.List;

import com.dev.costurartbaby.entities.dto.CategoriaRequest;
import com.dev.costurartbaby.entities.dto.CategoriaResponse;

public interface CategoriaService {
    List<CategoriaResponse> getAllCategorias();
    CategoriaResponse getCategoriaById(Long id);
    CategoriaResponse atualizarCategoria(Long id, CategoriaRequest request);
    void deletarCategoria(Long id);
    void adicionarProduto(Long categoriaId, Long produtoId);
    void removerProduto(Long categoriaId, Long produtoId);
    void criarCategoria(CategoriaRequest request);
}
