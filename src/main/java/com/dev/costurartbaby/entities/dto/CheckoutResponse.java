package com.dev.costurartbaby.entities.dto;

import java.util.UUID;

public record CheckoutResponse(
        UUID pedidoId,
        long yampiPaymentLinkId,
        String linkUrl
) {
}