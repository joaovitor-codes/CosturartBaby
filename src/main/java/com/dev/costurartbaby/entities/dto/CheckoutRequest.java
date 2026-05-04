package com.dev.costurartbaby.entities.dto;

import java.util.List;
import java.util.UUID;

public record CheckoutRequest(
        UUID clienteId,
        List<CheckoutItemRequest> itens
) {
}