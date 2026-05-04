package com.dev.costurartbaby.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dev.costurartbaby.config.exception.IntegrationException;
import com.dev.costurartbaby.entities.dto.YampiPaymentLinkResponse;
import com.dev.costurartbaby.entities.dto.YampiSkuQuantity;
import com.dev.costurartbaby.entities.produto.ProdutoEntity;
import com.dev.costurartbaby.service.YampiService;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

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

    @Value("${yampi.api.brand-id}")
    private Integer brandId;

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
        return apiUrl + "/" + alias + "/catalog/products";
    }

    private String skusUrl() {
        return apiUrl + "/" + alias + "/catalog/skus";
    }

    private String paymentLinkUrl() {
        return apiUrl + "/" + alias + "/checkout/payment-link";
    }

    private void validateConfig() {
        if (alias == null || alias.isBlank()) {
            throw new IntegrationException("Config yampi.api.alias não informada");
        }
        if (secretKey == null || secretKey.isBlank() || apiToken == null || apiToken.isBlank()) {
            throw new IntegrationException("Config de autenticação da Yampi não informada");
        }
        if (brandId == null || brandId <= 0) {
            throw new IntegrationException("Config yampi.api.brand-id inválida");
        }
    }

    private long readId(JsonNode body) {
        if (body == null) {
            return 0L;
        }
        long id = body.path("id").asLong(0);
        if (id > 0) {
            return id;
        }
        return body.path("data").path("id").asLong(0);
    }

    private float[] parseDimensoes(String dimensoes) {
        if (dimensoes == null || dimensoes.isBlank()) {
            return new float[]{0f, 0f, 0f};
        }
        String[] parts = dimensoes.toLowerCase().replace("cm", "").split("x");
        if (parts.length != 3) {
            return new float[]{0f, 0f, 0f};
        }
        try {
            return new float[]{Float.parseFloat(parts[0].trim()), Float.parseFloat(parts[1].trim()), Float.parseFloat(parts[2].trim())};
        } catch (NumberFormatException e) {
            return new float[]{0f, 0f, 0f};
        }
    }

    private Long criarProdutoMinimo(ProdutoEntity produto) {
        validateConfig();

        Map<String, Object> body = new HashMap<>();
        body.put("simple", true);
        body.put("brand_id", brandId);
        body.put("active", true);
        body.put("name", produto.getNome());
        body.put("description", produto.getDescricao());

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, getHeaders());

        try {
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(productsUrl(), requestEntity, JsonNode.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new IntegrationException("Falha ao criar produto no Yampi. Status: " + response.getStatusCode());
            }

            long id = readId(response.getBody());
            if (id <= 0) {
                throw new IntegrationException("Falha ao criar produto no Yampi: resposta sem id");
            }

            return id;
        } catch (IntegrationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao criar produto no Yampi", e);
            throw new IntegrationException("Erro ao criar produto no Yampi: " + e.getMessage());
        }
    }

    private Long criarSkuMinimo(Long yampiProductId, ProdutoEntity produto) {
        validateConfig();

        float[] dims = parseDimensoes(produto.getDimensoes());
        Map<String, Object> body = new HashMap<>();
        body.put("product_id", yampiProductId);
        body.put("sku", produto.getId() == null ? "PROD" : "PROD-" + produto.getId());
        body.put("price_cost", 0f);
        body.put("price_sale", produto.getPreco() == null ? 0f : produto.getPreco().floatValue());
        body.put("weight", 0f);
        body.put("height", dims[1]);
        body.put("width", dims[0]);
        body.put("length", dims[2]);
        body.put("quantity_managed", true);
        body.put("availability", produto.getEstoque());
        body.put("availability_soldout", -1);
        body.put("blocked_sale", false);
        body.put("variations_values_ids", List.of());

        if (produto.getImagemUrl() != null && !produto.getImagemUrl().isBlank()) {
            body.put("images", List.of(Map.of("url", produto.getImagemUrl())));
        }

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, getHeaders());

        try {
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(skusUrl(), requestEntity, JsonNode.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new IntegrationException("Falha ao criar SKU no Yampi. Status: " + response.getStatusCode());
            }

            long id = readId(response.getBody());
            if (id <= 0) {
                throw new IntegrationException("Falha ao criar SKU no Yampi: resposta sem id");
            }

            return id;
        } catch (IntegrationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao criar SKU no Yampi", e);
            throw new IntegrationException("Erro ao criar SKU no Yampi: " + e.getMessage());
        }
    }

    @Override
    public Long garantirYampiSkuId(ProdutoEntity produto) {
        if (produto.getYampiSkuId() != null) {
            return produto.getYampiSkuId();
        }

        Long yampiProductId = produto.getYampiProductId();
        if (yampiProductId == null) {
            yampiProductId = criarProdutoMinimo(produto);
            produto.setYampiProductId(yampiProductId);
        }

        Long yampiSkuId = criarSkuMinimo(yampiProductId, produto);
        produto.setYampiSkuId(yampiSkuId);
        return yampiSkuId;
    }

    @Override
    public YampiPaymentLinkResponse criarLinkPagamento(String name, List<YampiSkuQuantity> skus) {
        validateConfig();

        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("active", true);
        body.put("skus", skus.stream().map(i -> Map.of("id", i.id(), "quantity", i.quantity())).toList());

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, getHeaders());

        try {
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(paymentLinkUrl(), requestEntity, JsonNode.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new IntegrationException("Falha ao criar Link de Pagamento no Yampi. Status: " + response.getStatusCode());
            }

            JsonNode responseBody = response.getBody();
            long paymentLinkId = readId(responseBody);
            String linkUrl = responseBody == null ? null : responseBody.path("link_url").asText(null);

            if (paymentLinkId <= 0 || linkUrl == null || linkUrl.isBlank()) {
                throw new IntegrationException("Falha ao criar Link de Pagamento no Yampi: resposta inválida");
            }

            return new YampiPaymentLinkResponse(paymentLinkId, linkUrl);
        } catch (IntegrationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao criar Link de Pagamento no Yampi", e);
            throw new IntegrationException("Erro ao criar Link de Pagamento no Yampi: " + e.getMessage());
        }
    }
}
