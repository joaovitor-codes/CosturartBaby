package com.dev.costurartbaby.resource;

import com.dev.costurartbaby.service.ProdutoService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookResource {

    private final ProdutoService produtoService;
    private final ObjectMapper objectMapper;

    @Value("${yampi.webhook.secret}")
    private String secretKey;

    public WebhookResource(ProdutoService produtoService, ObjectMapper objectMapper) {
        this.produtoService = produtoService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/yampi")
    public ResponseEntity<Void> receberWebhook(
            @RequestHeader(value = "x-yampi-hmac-sha256", required = false) String assinaturaRecebida,
            @RequestBody String rawPayload) { // Recebe como String pura para validar a assinatura

        if (assinaturaRecebida == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            // 1. Configurar o algoritmo de encriptação HMAC-SHA256
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);

            // 2. Encriptar o payload recebido usando a sua chave secreta
            byte[] hashBytes = mac.doFinal(rawPayload.getBytes(StandardCharsets.UTF_8));

            // 3. Converter para Base64 (que é o formato que a Yampi envia)
            String assinaturaCalculada = Base64.getEncoder().encodeToString(hashBytes);

            // 4. Comparar as assinaturas
            if (!assinaturaCalculada.equals(assinaturaRecebida)) {
                System.err.println("Assinatura do Webhook inválida! Possível tentativa de fraude.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // 5. Se a assinatura estiver correta, convertemos a String para JsonNode e processamos
            JsonNode jsonNode = objectMapper.readTree(rawPayload);
            produtoService.processarWebHookYampi(jsonNode);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}