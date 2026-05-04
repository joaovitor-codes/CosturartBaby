package com.dev.costurartbaby.entities.dto;

import java.math.BigDecimal;
import java.util.Optional;

public record ProdutoUpdate(
        Optional<String> nome,
        Optional<String> descricao,
        Optional<BigDecimal> preco,
        Optional<Integer> estoque,
        Optional<String> dimensoes,
        Optional<String> imagemUrl,
        Optional<Long> categoriaId
) {
}
