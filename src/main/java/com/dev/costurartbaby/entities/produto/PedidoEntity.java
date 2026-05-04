package com.dev.costurartbaby.entities.produto;

import com.dev.costurartbaby.entities.cliente.ClienteEntity;
import com.dev.costurartbaby.entities.enums.StatusPedido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private Long yampiOrderId;

    @Column(unique = true)
    private Long yampiPaymentLinkId;

    private String yampiPaymentLinkUrl;

    @ManyToOne
    @JoinColumn(name = "cliente_id", referencedColumnName = "id")
    private ClienteEntity cliente;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProdutoItemEntity> itens;

    private LocalDateTime data;
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    private StatusPedido status;
}
