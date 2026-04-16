package com.dev.costurartbaby.entities.dto;

import com.dev.costurartbaby.entities.ContaEntity;

public record ContaResponse(
        java.util.UUID id,
        String login
) {
    public ContaResponse(ContaEntity entity) {
        this(entity.getId(), entity.getUsername());
    }
}
