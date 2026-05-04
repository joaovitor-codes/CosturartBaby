package com.dev.costurartbaby.entities.dto;

import com.dev.costurartbaby.entities.produto.ProdutoEntity;
import java.math.BigDecimal;

public record ProdutoResponse(
    Long id,
    Long yampiProductId,
    Long yampiSkuId,
    String nome,
    String descricao,
    BigDecimal preco,
    int estoque,
    String dimensoes,
    String imagemUrl
) {
    public ProdutoResponse(ProdutoEntity entity) {
        this(
            entity.getId(),
            entity.getYampiProductId(),
            entity.getYampiSkuId(),
            entity.getNome(),
            entity.getDescricao(),
            entity.getPreco(),
            entity.getEstoque(),
            entity.getDimensoes(),
            entity.getImagemUrl()
        );
    }
}