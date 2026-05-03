package com.dev.costurartbaby.entities.dto;

import java.math.BigDecimal;

public record ProdutoRequest(
    String nome,
    String descricao,
    BigDecimal preco,
    int estoque,
    String dimensoes,
    String imagemUrl,
    Long categoriaId
) {}