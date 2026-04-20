package com.dev.costurartbaby.entities.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;


public record ClienteRequest(
        @NotBlank String nome,
        @NotBlank @CPF String cpf,
        @NotBlank LocalDate dataNascimento,
        @NotBlank String telefone
        ) {
}
