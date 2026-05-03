package com.dev.costurartbaby.service;

import com.dev.costurartbaby.entities.produto.ProdutoEntity;

public interface YampiService {
    Long criarProduto(ProdutoEntity produto);
    void atualizarProduto(ProdutoEntity produto);
}
