package com.dev.costurartbaby.entities.dto;

import java.time.LocalDate;
import java.util.Optional;

public record ClienteUpdate(
        Optional<String> nome,
        Optional<String> cpf,
        Optional<LocalDate> dataNascimento
) {
}
