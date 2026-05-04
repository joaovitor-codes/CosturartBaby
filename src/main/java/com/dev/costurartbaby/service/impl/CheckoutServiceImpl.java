package com.dev.costurartbaby.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dev.costurartbaby.config.exception.BusinessException;
import com.dev.costurartbaby.entities.cliente.ClienteEntity;
import com.dev.costurartbaby.entities.dto.CheckoutItemRequest;
import com.dev.costurartbaby.entities.dto.CheckoutRequest;
import com.dev.costurartbaby.entities.dto.CheckoutResponse;
import com.dev.costurartbaby.entities.dto.YampiPaymentLinkResponse;
import com.dev.costurartbaby.entities.dto.YampiSkuQuantity;
import com.dev.costurartbaby.entities.enums.StatusPedido;
import com.dev.costurartbaby.entities.produto.PedidoEntity;
import com.dev.costurartbaby.entities.produto.ProdutoEntity;
import com.dev.costurartbaby.entities.produto.ProdutoItemEntity;
import com.dev.costurartbaby.repository.ClienteRepository;
import com.dev.costurartbaby.repository.PedidoRepository;
import com.dev.costurartbaby.repository.ProdutoRepository;
import com.dev.costurartbaby.service.CheckoutService;
import com.dev.costurartbaby.service.YampiService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CheckoutServiceImpl implements CheckoutService {
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final PedidoRepository pedidoRepository;
    private final YampiService yampiService;

    public CheckoutServiceImpl(ClienteRepository clienteRepository, ProdutoRepository produtoRepository, PedidoRepository pedidoRepository,
        YampiService yampiService) {
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.pedidoRepository = pedidoRepository;
        this.yampiService = yampiService;
    }

    @Override
    @Transactional
    public CheckoutResponse criarLinkPagamento(CheckoutRequest request) {
        if(request.itens() == null || request.itens().isEmpty()) {
            throw new BusinessException("O carrinho está vazio");
        }

        ClienteEntity cliente = clienteRepository.findById(request.clienteId())
            .orElseThrow(() -> new BusinessException("O cliente não foi encontrado"));

        PedidoEntity pedido = PedidoEntity.builder()
            .cliente(cliente)
            .data(LocalDateTime.now())
            .status(StatusPedido.PENDENTE)
            .build();

        List<ProdutoItemEntity> itens = new ArrayList<>();
        List<YampiSkuQuantity> yampiSkus = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for(CheckoutItemRequest item : request.itens()) {
            if (item.quantidade() <= 0) {
                throw new BusinessException("A quantidade inválida");
            }
            
            ProdutoEntity produto = produtoRepository.findById(item.produtoId())
                .orElseThrow(() -> new BusinessException("O produto não foi encontrado: " + item.produtoId()));

            if (produto.getEstoque() < item.quantidade()) {
                throw new BusinessException("Quantidade em estoque insuficiente para produto: " + item.produtoId());
            }

            Long yampiSkulId = produto.getYampiProductId();
            if (yampiSkulId == null) {
                yampiSkulId = yampiService.garantirYampiSkuId(produto);
                produtoRepository.save(produto);
            }

            ProdutoItemEntity produtoItem = ProdutoItemEntity.builder()
                .pedido(pedido)
                .produto(produto)
                .quantidade(item.quantidade())
                .precoAtual(produto.getPreco())
                .build();

            itens.add(produtoItem);
            yampiSkus.add(new YampiSkuQuantity(yampiSkulId, item.quantidade()));
            total = total.add(produto.getPreco().multiply(BigDecimal.valueOf(item.quantidade())));
        }
        
        pedido.setItens(itens);
        pedido.setValor(total);

        PedidoEntity savedPedido = pedidoRepository.save(pedido);

        String name = "Pedido " + savedPedido.getId();
        YampiPaymentLinkResponse paymentLink = yampiService.criarLinkPagamento(name, yampiSkus);
        savedPedido.setYampiPaymentLinkId(paymentLink.id());
        savedPedido.setYampiPaymentLinkUrl(paymentLink.linkUrl());
        pedidoRepository.save(savedPedido);

        return new CheckoutResponse(savedPedido.getId(), paymentLink.id(), paymentLink.linkUrl());  
    }
}
