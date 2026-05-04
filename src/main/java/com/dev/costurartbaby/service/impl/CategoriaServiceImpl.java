package com.dev.costurartbaby.service.impl;

import com.dev.costurartbaby.config.exception.BusinessException;
import com.dev.costurartbaby.config.exception.ResourceNotFoundException;
import com.dev.costurartbaby.entities.dto.CategoriaRequest;
import com.dev.costurartbaby.entities.dto.CategoriaResponse;
import com.dev.costurartbaby.entities.produto.CategoriaEntity;
import com.dev.costurartbaby.entities.produto.ProdutoEntity;
import com.dev.costurartbaby.repository.CategoriaRepository;
import com.dev.costurartbaby.repository.ProdutoRepository;
import com.dev.costurartbaby.service.CategoriaService;

import java.util.List;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoriaServiceImpl implements CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final ProdutoRepository produtoRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository, ProdutoRepository produtoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.produtoRepository = produtoRepository;
    }
    
    @Override
    @Transactional
    public List<CategoriaResponse> getAllCategorias(){
        return categoriaRepository.findAll()
                .stream()
                .map(c -> new CategoriaResponse(c.getId(), c.getNome()))
                .toList();
    }

    @Override
    @Transactional
    public void criarCategoria(CategoriaRequest request){
        if (categoriaRepository.existsByNome(request.nome())) {
            throw new BusinessException("Categoria já existe");
        }


        categoriaRepository.save(CategoriaEntity.builder()
        .nome(request.nome())
        .build());
    }

    @Override
    @Transactional
    public void adicionarProduto(Long categoriaId, Long produtoId){
        ProdutoEntity produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
        CategoriaEntity categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

        categoria.getProdutos().add(produto);
        categoriaRepository.save(categoria);
    }

    @Override
    @Transactional
    public void removerProduto(Long categoriaId, Long produtoId){
        ProdutoEntity produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
        CategoriaEntity categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

        categoria.getProdutos().remove(produto);
        categoriaRepository.save(categoria);
    }

    @Override
    @Transactional
    public void deletarCategoria(Long id){
        CategoriaEntity categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
                
        if (!categoria.getProdutos().isEmpty()) {
            throw new BusinessException("Categoria não pode ser deletada pois possui produtos associados");
        }

        categoriaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CategoriaResponse getCategoriaById(Long id){
        CategoriaEntity categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

        return new CategoriaResponse(categoria.getId(), categoria.getNome());
    }

    @Override
    @Transactional
    public CategoriaResponse atualizarCategoria(Long id, CategoriaRequest request){
        CategoriaEntity categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));


        if (categoriaRepository.existsByNome(request.nome())) {
            throw new BusinessException("Categoria já existe");
        }

        categoria.setNome(request.nome());
        categoriaRepository.save(categoria);

        return new CategoriaResponse(categoria.getId(), categoria.getNome());
    }
}
