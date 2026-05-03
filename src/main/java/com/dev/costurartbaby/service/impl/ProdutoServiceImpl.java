package com.dev.costurartbaby.service.impl;

import com.dev.costurartbaby.config.exception.BusinessException;
import com.dev.costurartbaby.config.exception.IntegrationException;
import com.dev.costurartbaby.entities.dto.ProdutoRequest;
import com.dev.costurartbaby.entities.produto.CategoriaEntity;
import com.dev.costurartbaby.entities.produto.ProdutoEntity;
import com.dev.costurartbaby.repository.CategoriaRepository;
import com.dev.costurartbaby.repository.ProdutoRepository;
import com.dev.costurartbaby.service.ProdutoService;
import com.dev.costurartbaby.service.YampiService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
public class ProdutoServiceImpl implements ProdutoService {
    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final YampiService yampiService;

    public ProdutoServiceImpl(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository,
                               YampiService yampiService) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
        this.yampiService = yampiService;
    }

    @Override
    @Transactional
    public void processarWebHookYampi(JsonNode payLoad) {
        JsonNode resource = payLoad.path("resource");

        if (resource.isMissingNode() || resource.path("id").isMissingNode()) {
            log.error("Webhook recebido sem dados de resource ou ID válido na Yampi.");
            return;
        }

        Long yampiId = resource.path("id").asLong();

        ProdutoEntity produto = produtoRepository.findByYampId(yampiId)
                .orElse(new ProdutoEntity());

        produto.setYampId(yampiId);

        // Protegendo os textos com asText("default") ou verificando se é nulo
        produto.setNome(resource.path("name").asText("Produto sem nome"));

        JsonNode descricaoNode = resource.path("description");
        produto.setDescricao(descricaoNode.isNull() || descricaoNode.isMissingNode() ? "" : descricaoNode.asText());

        // Corrigindo a busca pelo SKU (a Yampi envia dentro de "data")
        JsonNode skusData = resource.path("skus").path("data");
        if (skusData.isArray() && !skusData.isEmpty()) {
            JsonNode firstSku = skusData.get(0);

            produto.setPreco(new BigDecimal(firstSku.path("price_sale").asText("0")));
            produto.setEstoque(firstSku.path("total_in_stock").asInt(0));

            String dimensoes = firstSku.path("width").asText("0") + "x" +
                    firstSku.path("height").asText("0") + "x" +
                    firstSku.path("length").asText("0");
            produto.setDimensoes(dimensoes);
        } else {
            // Valores padrão se o produto vier sem SKU ainda
            produto.setPreco(BigDecimal.ZERO);
            produto.setEstoque(0);
            produto.setDimensoes("0x0x0");
        }

        // Corrigindo a imagem e protegendo contra Nulos
        JsonNode firstImage = resource.path("firstImage").path("data");
        if (!firstImage.isMissingNode() && !firstImage.isNull()) {
            produto.setImagemUrl(firstImage.path("medium").path("url").asText(null));
        } else {
            produto.setImagemUrl(null);
        }

        produtoRepository.save(produto);
        log.info("Webhook Yampi: Produto processado com sucesso! ID Yampi: {}", yampiId);
    }

    public void createProduto(ProdutoRequest produtoRequest){
        log.info("Criando produto: {}", produtoRequest);

        CategoriaEntity categoria = categoriaRepository.findById(produtoRequest.categoriaId())
                .orElseThrow(() -> {
                    log.warn("Categoria não encontrada: {}", produtoRequest.categoriaId());
                    return new BusinessException("Categoria não encontrada");
                });

        ProdutoEntity produto = ProdutoEntity.builder()
                .nome(produtoRequest.nome())
                .descricao(produtoRequest.descricao())
                .preco(produtoRequest.preco())
                .estoque(produtoRequest.estoque())
                .dimensoes(produtoRequest.dimensoes())
                .imagemUrl(produtoRequest.imagemUrl())
                .categoria(categoria)
                .build();

        produtoRepository.save(produto);
        try {
            Long yampiId = yampiService.criarProduto(produto);
            log.info("Produto criado na Yampi: {}", yampiId);
            produto.setYampId(yampiId);
            produtoRepository.save(produto);
        } catch (IntegrationException e) {
            log.error("Erro ao criar produto na Yampi: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    public void updateProduto(Long id, ProdutoRequest produtoRequest){
        log.info("Atualizando produto: {}", id);
        ProdutoEntity produto = produtoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Produto não encontrado: {}", id);
                    return new BusinessException("Produto não encontrado");
                });

        yampiService.atualizarProduto(produto);
        produtoRepository.save(produto);
    }
}