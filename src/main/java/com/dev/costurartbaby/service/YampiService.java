package com.dev.costurartbaby.service;

import com.dev.costurartbaby.entities.dto.YampiPaymentLinkResponse;
import com.dev.costurartbaby.entities.dto.YampiSkuQuantity;
import com.dev.costurartbaby.entities.produto.ProdutoEntity;

import java.util.List;

public interface YampiService {
    Long garantirYampiSkuId(ProdutoEntity produto);
    YampiPaymentLinkResponse criarLinkPagamento(String name, List<YampiSkuQuantity> skus);
}
