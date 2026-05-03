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

    @Column(unique = true)
    private Long yampId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private int estoque;
    private String dimensoes;
    private String imagemUrl;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "categoria_id")
    private CategoriaEntity categoria;
}
