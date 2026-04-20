package com.dev.costurartbaby.entities.produto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_produto_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoItemEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private ProdutoEntity produto;

    @ManyToOne
    @JoinColumn(name = "pedido_id", referencedColumnName = "id")
    private PedidoEntity pedido;

    @Embedded
    private Personalizacao personalizacao;
    private int quantidade;
    private BigDecimal precoAtual;

}
