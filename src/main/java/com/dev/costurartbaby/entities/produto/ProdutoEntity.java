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
@Table(name = "tb_produto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "yamp_id", unique = true)
    private Long yampiProductId;

    @Column(name = "yampi_sku_id", unique = true)
    private Long yampiSkuId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private int estoque;
    private String dimensoes;
    private String imagemUrl;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private CategoriaEntity categoria;
}
