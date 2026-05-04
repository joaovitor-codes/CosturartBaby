package com.dev.costurartbaby.service.impl;

import com.dev.costurartbaby.config.exception.BusinessException;
import com.dev.costurartbaby.entities.dto.ProdutoRequest;
import com.dev.costurartbaby.entities.dto.ProdutoResponse;
import com.dev.costurartbaby.entities.dto.ProdutoUpdate;
import com.dev.costurartbaby.entities.enums.StatusPedido;
import com.dev.costurartbaby.entities.produto.CategoriaEntity;
import com.dev.costurartbaby.entities.produto.PedidoEntity;
import com.dev.costurartbaby.entities.produto.ProdutoEntity;
import com.dev.costurartbaby.entities.produto.ProdutoItemEntity;
import com.dev.costurartbaby.repository.CategoriaRepository;
import com.dev.costurartbaby.repository.ClienteRepository;
import com.dev.costurartbaby.repository.PedidoRepository;
import com.dev.costurartbaby.repository.ProdutoRepository;
import com.dev.costurartbaby.service.ProdutoService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ProdutoServiceImpl implements ProdutoService {
    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;

    public ProdutoServiceImpl(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository, PedidoRepository pedidoRepository, ClienteRepository clienteRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional
    public void processarWebHookYampi(JsonNode payLoad) {
        String event = payLoad.path("event").asText("");
        JsonNode resource = payLoad.path("resource");

        if (!event.startsWith("order.")) {
            log.info("Webhook Yampi ignorado. event={}", event);
            return;
        }

        if (resource.isMissingNode() || resource.path("id").isMissingNode()) {
            log.error("Webhook de pedido recebido sem resource.id");
            return;
        }

        Long yampiOrderId = resource.path("id").asLong();

        PedidoEntity pedido = pedidoRepository.findByYampiOrderId(yampiOrderId)
                .orElseGet(() -> {
                    PedidoEntity novo = new PedidoEntity();
                    novo.setYampiOrderId(yampiOrderId);
                    novo.setData(LocalDateTime.now());
                    novo.setStatus(StatusPedido.PENDENTE);
                    return novo;
                });

        String cpf = resource.path("customer").path("data").path("cpf").asText(null);
        if (pedido.getCliente() == null && cpf != null && !cpf.isBlank()) {
            clienteRepository.findByCpf(cpf).ifPresent(pedido::setCliente);
        }

        BigDecimal valorTotal = BigDecimal.valueOf(resource.path("value_total").asDouble(0.0));
        if (valorTotal.compareTo(BigDecimal.ZERO) > 0) {
            pedido.setValor(valorTotal);
        }

        StatusPedido statusNovo = mapStatus(event, resource);
        boolean jaAprovado = pedido.getStatus() == StatusPedido.APROVADO;
        pedido.setStatus(statusNovo);

        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            pedido.setItens(mapItens(resource, pedido));
        }

        PedidoEntity saved = pedidoRepository.save(pedido);

        if (!jaAprovado && saved.getStatus() == StatusPedido.APROVADO) {
            baixarEstoque(saved);
        }

        log.info("Webhook Yampi processado. event={} yampiOrderId={} status={}", event, yampiOrderId, saved.getStatus());
    }

    private StatusPedido mapStatus(String event, JsonNode resource) {
        if (event.equals("order.paid")) {
            return StatusPedido.APROVADO;
        }
        if (event.equals("order.created")) {
            return StatusPedido.PENDENTE;
        }

        String statusAlias = resource.path("status").path("data").path("alias").asText("");
        return switch (statusAlias) {
            case "paid" -> StatusPedido.APROVADO;
            case "cancelled" -> StatusPedido.CANCELADO;
            case "refused" -> StatusPedido.REJEITADO;
            case "waiting_payment" -> StatusPedido.PENDENTE;
            default -> StatusPedido.PENDENTE;
        };
    }

    private List<ProdutoItemEntity> mapItens(JsonNode resource, PedidoEntity pedido) {
        List<ProdutoItemEntity> itens = new ArrayList<>();
        JsonNode itemsData = resource.path("items").path("data");
        if (!itemsData.isArray()) {
            return itens;
        }

        for (JsonNode item : itemsData) {
            long yampiSkuId = item.path("sku_id").asLong(0);
            int quantidade = item.path("quantity").asInt(0);
            if (yampiSkuId <= 0 || quantidade <= 0) {
                continue;
            }

            ProdutoEntity produto = produtoRepository.findByYampiSkuId(yampiSkuId).orElse(null);
            if (produto == null) {
                log.warn("SKU da Yampi não mapeado no catálogo local. yampiSkuId={}", yampiSkuId);
                continue;
            }

            BigDecimal preco = BigDecimal.valueOf(item.path("price").asDouble(0.0));

            ProdutoItemEntity produtoItem = ProdutoItemEntity.builder()
                    .pedido(pedido)
                    .produto(produto)
                    .quantidade(quantidade)
                    .precoAtual(preco)
                    .build();

            itens.add(produtoItem);
        }

        return itens;
    }

    private void baixarEstoque(PedidoEntity pedido) {
        if (pedido.getItens() == null) {
            return;
        }

        for (ProdutoItemEntity item : pedido.getItens()) {
            ProdutoEntity produto = item.getProduto();
            if (produto == null) {
                continue;
            }

            int novoEstoque = Math.max(0, produto.getEstoque() - item.getQuantidade());
            produto.setEstoque(novoEstoque);
            produtoRepository.save(produto);
            log.info("Estoque atualizado para {} em produto {} (YampiProductId={})",
                    novoEstoque, produto.getNome(), produto.getYampiProductId());
        }
    }

    @Override
    @Transactional
    public ProdutoResponse createProduto(ProdutoRequest request) {
        log.info("Criando produto: {}", request);

        if (request.categoriaId() == null) {
            log.warn("categoriaId não informado");
            throw new BusinessException("categoriaId é obrigatório");
        }

        if (request.categoriaId() == null) {
            log.warn("categoriaId não informado para PUT. produtoId={}", request.categoriaId());
            throw new BusinessException("categoriaId é obrigatório");
        }

        CategoriaEntity categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> {
                    log.warn("Categoria não encontrada: {}", request.categoriaId());
                    return new BusinessException("Categoria não encontrada");
                });

        ProdutoEntity produto = ProdutoEntity.builder()
                .nome(request.nome())
                .descricao(request.descricao())
                .preco(request.preco())
                .estoque(request.estoque())
                .dimensoes(request.dimensoes())
                .imagemUrl(request.imagemUrl())
                .categoria(categoria)
                .build();

        ProdutoEntity saved = produtoRepository.save(produto);
        log.info("Produto criado com sucesso: {}", saved.getId());
        return new ProdutoResponse(saved);
    }
    
    @Override
    @Transactional
    public ProdutoResponse updateProdutoPut(Long id, ProdutoRequest request) {
        log.info("Atualizando produto (PUT): {}", id);

        ProdutoEntity produto = produtoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Produto não encontrado: {}", id);
                    return new BusinessException("Produto não encontrado");
                });

        CategoriaEntity categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> {
                    log.warn("Categoria não encontrada: {}", request.categoriaId());
                    return new BusinessException("Categoria não encontrada");
                });

        produto.setNome(request.nome());
        produto.setDescricao(request.descricao());
        produto.setPreco(request.preco());
        produto.setEstoque(request.estoque());
        produto.setDimensoes(request.dimensoes());
        produto.setImagemUrl(request.imagemUrl());
        produto.setCategoria(categoria);

        ProdutoEntity saved = produtoRepository.save(produto);
        return new ProdutoResponse(saved);
    }

    @Override
    @Transactional
    public ProdutoResponse updateProdutoPatch(Long id, ProdutoUpdate request) {
        log.info("Atualizando produto (PATCH): {}", id);

        ProdutoEntity produto = produtoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Produto não encontrado: {}", id);
                    return new BusinessException("Produto não encontrado");
                });

        request.nome().ifPresent(produto::setNome);
        request.descricao().ifPresent(produto::setDescricao);
        request.preco().ifPresent(produto::setPreco);
        request.estoque().ifPresent(produto::setEstoque);
        request.dimensoes().ifPresent(produto::setDimensoes);
        request.imagemUrl().ifPresent(produto::setImagemUrl);

        if (request.categoriaId().isPresent()) {
            Long categoriaId = request.categoriaId().get();
            CategoriaEntity categoria = categoriaRepository.findById(categoriaId)
                    .orElseThrow(() -> {
                        log.warn("Categoria não encontrada: {}", categoriaId);
                        return new BusinessException("Categoria não encontrada");
                    });
            produto.setCategoria(categoria);
        }

        ProdutoEntity saved = produtoRepository.save(produto);
        return new ProdutoResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProdutoResponse getProdutoById(Long id) {
        ProdutoEntity produto = produtoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Produto não encontrado: {}", id);
                    return new BusinessException("Produto não encontrado");
                });

        return new ProdutoResponse(produto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProdutoResponse> getProdutos(int page, int size) {
        return produtoRepository.findAll(PageRequest.of(page, size))
                .map(ProdutoResponse::new);
    }

    @Override
    @Transactional
    public void deleteProduto(Long id) {
        ProdutoEntity produto = produtoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Produto não encontrado: {}", id);
                    return new BusinessException("Produto não encontrado");
                });

        produtoRepository.delete(produto);
        log.info("Produto deletado com sucesso: {}", produto.getId());
    }
}
