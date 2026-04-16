package com.dev.costurartbaby.entities.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ContaRequest(
        @NotBlank(message = "Email obrigatório")
        @Email(message = "Email invalido")
        String login,
        @NotBlank(message = "Senha obrigatória")
        String senha
) {
}
