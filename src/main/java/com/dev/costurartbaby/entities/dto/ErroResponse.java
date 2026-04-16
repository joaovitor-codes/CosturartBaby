package com.dev.costurartbaby.entities.dto;

public record ErroResponse(
        long timestamp,
        int status,
        String erro,
        String mensagem,
        String path
) {}
