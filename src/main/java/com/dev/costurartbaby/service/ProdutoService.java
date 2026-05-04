package com.dev.costurartbaby.service;

import com.dev.costurartbaby.entities.dto.ProdutoRequest;
import com.dev.costurartbaby.entities.dto.ProdutoResponse;
import com.dev.costurartbaby.entities.dto.ProdutoUpdate;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;

public interface ProdutoService {
    ProdutoResponse createProduto(ProdutoRequest request);
    ProdutoResponse updateProdutoPut(Long id, ProdutoRequest request);
    ProdutoResponse updateProdutoPatch(Long id, ProdutoUpdate request);
    ProdutoResponse getProdutoById(Long id);
    Page<ProdutoResponse> getProdutos(int page, int size);
    void deleteProduto(Long id);
    void processarWebHookYampi(JsonNode payLoad);
}
