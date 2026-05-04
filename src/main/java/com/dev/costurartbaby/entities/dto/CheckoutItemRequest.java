package com.dev.costurartbaby.entities.dto;

public record CheckoutItemRequest(
        Long produtoId,
        int quantidade
) {
}