package com.dev.costurartbaby.entities.cliente;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "tb_telefone")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelefoneEntity implements Serializable {
    @Serial
    private final static long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "cliente_id", referencedColumnName = "id")
    private ClienteEntity cliente;
    private String numero;
}
