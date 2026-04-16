package com.dev.costurartbaby.entities.dto;

import java.util.Optional;

public record ContaUpdate(
            Optional<String> login,
            Optional<String> senha
) {
}
