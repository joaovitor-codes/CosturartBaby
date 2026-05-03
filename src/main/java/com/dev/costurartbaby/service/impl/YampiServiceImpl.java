package com.dev.costurartbaby.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dev.costurartbaby.config.exception.IntegrationException;
import com.dev.costurartbaby.entities.produto.ProdutoEntity;
import com.dev.costurartbaby.service.YampiService;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

@Service
@Slf4j
public class YampiServiceImpl implements YampiService {
    private final RestTemplate restTemplate;

    @Value("${yampi.api.token}")
    private String apiToken;

    @Value("${yampi.api.secret-key}")
    private String secretKey;

    @Value("${yampi.api.alias}")
    private String alias;

    @Value("${yampi.api.url}")
    private String apiUrl;

    public YampiServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Secret-Key", secretKey);
        headers.set("User-Token", apiToken);
        return headers;
    }

    private String productsUrl() {
        if (alias == null || alias.isBlank()) {
            throw new IntegrationException("Config yampi.api.alias não informada");
        }
        return apiUrl + "/" + alias + "/catalog/products";
    }

    @Override
    public Long criarProduto(ProdutoEntity produto){
        Map<String, Object> body = new HashMap<>();
        body.put("simple", true);
        body.put("active", true);
        body.put("name", produto.getNome());
        body.put("description", produto.getDescricao());
        if (produto.getDimensoes() != null && !produto.getDimensoes().isBlank()) {
            body.put("measures", produto.getDimensoes());
        }

        Map<String, Object> sku = new HashMap<>();
        if (produto.getId() != null) {
            sku.put("sku", "PROD-" + produto.getId());
        }
        if (produto.getPreco() != null) {
            sku.put("price_sale", produto.getPreco());
        }
        sku.put("quantity_managed", true);
        sku.put("availability", produto.getEstoque());

        if (produto.getImagemUrl() != null && !produto.getImagemUrl().isBlank()) {
            List<Map<String, Object>> images = new ArrayList<>();
            images.add(Map.of("url", produto.getImagemUrl()));
            sku.put("images", images);
        }

        List<Map<String, Object>> skus = new ArrayList<>();
        skus.add(sku);
        body.put("skus", skus);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, getHeaders());

        try{
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(productsUrl(), requestEntity, JsonNode.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new IntegrationException("Falha ao criar produto no Yampi. Status: " + response.getStatusCode());
            }

            JsonNode responseBody = response.getBody();
            long yampiId = responseBody == null ? 0L : responseBody.path("data").path("id").asLong(0);
            if (yampiId <= 0) {
                throw new IntegrationException("Falha ao criar produto no Yampi: resposta sem id");
            }

            return yampiId;
        }catch(IntegrationException e){
            throw e;
        }catch(Exception e){
            log.error("Erro ao criar produto no Yampi", e);
            throw new IntegrationException("Erro ao criar produto no Yampi: " + e.getMessage());
        }
    }

    @Override
    public void atualizarProduto(ProdutoEntity produto){
        if (produto.getYampId() == null) {
            throw new IntegrationException("Produto sem yampId para atualizar na Yampi");
        }

        Map<String, Object> body = new HashMap<>();
        body.put("simple", true);
        body.put("active", true);
        body.put("name", produto.getNome());
        body.put("description", produto.getDescricao());
        if (produto.getDimensoes() != null && !produto.getDimensoes().isBlank()) {
            body.put("measures", produto.getDimensoes());
        }

        Map<String, Object> sku = new HashMap<>();
        if (produto.getId() != null) {
            sku.put("sku", "PROD-" + produto.getId());
        }
        if (produto.getPreco() != null) {
            sku.put("price_sale", produto.getPreco());
        }
        sku.put("quantity_managed", true);
        sku.put("availability", produto.getEstoque());

        if (produto.getImagemUrl() != null && !produto.getImagemUrl().isBlank()) {
            List<Map<String, Object>> images = new ArrayList<>();
            images.add(Map.of("url", produto.getImagemUrl()));
            sku.put("images", images);
        }

        List<Map<String, Object>> skus = new ArrayList<>();
        skus.add(sku);
        body.put("skus", skus);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, getHeaders());

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(productsUrl() + "/" + produto.getYampId(), HttpMethod.PUT, entity, JsonNode.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new IntegrationException("Falha ao atualizar produto na Yampi. Status: " + response.getStatusCode());
            }
        } catch (IntegrationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao atualizar produto na Yampi", e);
            throw new IntegrationException("Erro ao atualizar produto na Yampi: " + e.getMessage());
        }
    }
}
