package com.dev.costurartbaby.entities.produto;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Personalizacao implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String cor;
    private String tecido;
    private String estampa;
    private BigDecimal precoDaPersonalizacao;
}
