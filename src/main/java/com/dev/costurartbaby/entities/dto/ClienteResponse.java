package com.dev.costurartbaby.entities.dto;

import java.util.List;

public record ClienteResponse(
        String id,
        String nome,
        String cpf,
        String dataNascimento,
        List<String> telefones
) {
}
