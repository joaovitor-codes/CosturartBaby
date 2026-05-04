package com.dev.costurartbaby.config;

import com.dev.costurartbaby.entities.cliente.ContaEntity;
import com.dev.costurartbaby.entities.cliente.ContaRole;
import com.dev.costurartbaby.entities.cliente.EnderecoEntity;
import com.dev.costurartbaby.entities.cliente.ClienteEntity;
import com.dev.costurartbaby.entities.cliente.TelefoneEntity;
import com.dev.costurartbaby.entities.produto.CategoriaEntity;
import com.dev.costurartbaby.entities.produto.ProdutoEntity;
import com.dev.costurartbaby.repository.CategoriaRepository;
import com.dev.costurartbaby.repository.ClienteRepository;
import com.dev.costurartbaby.repository.ContaRepository;
import com.dev.costurartbaby.repository.ProdutoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Configuration
@Profile("test")
public class TestConfig {

    @Bean
    public CommandLineRunner seedAdminUser(ContaRepository contaRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (contaRepository.findByLogin("admin@costurart.com") == null) {

                ContaEntity admin = ContaEntity.builder()
                        .login("admin@costurart.com")
                        .senha(passwordEncoder.encode("123456"))
                        .role(ContaRole.ADMIN)
                        .enabled(true)
                        .locked(false)
                        .build();

                contaRepository.save(admin);
                System.out.println("✅ [PERFIL DE TESTE] Administrador Padrão criado com sucesso!");
            }
        };
    }

    @Bean
    public CommandLineRunner seedClienteComTelefone(ClienteRepository clienteRepository, ContaRepository contaRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String cpf = "39053344705";
            String login = "user@costurart.com";

            if (clienteRepository.existsByCpf(cpf) || contaRepository.existsByLogin(login)) {
                return;
            }

            ContaEntity conta = ContaEntity.builder()
                    .login(login)
                    .senha(passwordEncoder.encode("123456"))
                    .role(ContaRole.USER)
                    .enabled(true)
                    .locked(false)
                    .build();

            TelefoneEntity telefone = TelefoneEntity.builder()
                    .numero("11999990000")
                    .build();

            EnderecoEntity endereco = EnderecoEntity.builder()
                    .logradouro("Rua Teste")
                    .numero("123")
                    .bairro("Bairro Teste")
                    .cidade("Cidade Teste")
                    .estado("SP")
                    .cep("12345-678")
                    .build();

            ClienteEntity cliente = ClienteEntity.builder()
                    .conta(conta)
                    .nome("Cliente Teste")
                    .cpf(cpf)
                    .dataNascimento(LocalDate.of(1995, 1, 1))
                    .telefones(List.of(telefone))
                    .enderecos(List.of(endereco))
                    .build();

            conta.setCliente(cliente);
            telefone.setCliente(cliente);
            endereco.setCliente(cliente);

            clienteRepository.save(cliente);
            System.out.println("✅ [PERFIL DE TESTE] Cliente com telefone criado com sucesso!");
        };
    }

    @Bean
    public CommandLineRunner seedProduto(CategoriaRepository categoriaRepository, ProdutoRepository produtoRepository) {
        return args -> {
            if (produtoRepository.count() > 0) {
                return;
            }

            CategoriaEntity categoria = CategoriaEntity.builder()
                    .nome("Enxoval")
                    .build();

            categoria = categoriaRepository.save(categoria);

            ProdutoEntity produto = ProdutoEntity.builder()
                    .nome("Kit Enxoval Bebê")
                    .descricao("Kit enxoval para bebê")
                    .preco(new BigDecimal("199.90"))
                    .estoque(10)
                    .dimensoes("10x20x30")
                    .imagemUrl("https://example.com/produto.jpg")
                    .categoria(categoria)
                    .yampiProductId(111111L)
                    .yampiSkuId(222222L)
                    .build();

            produtoRepository.save(produto);
            System.out.println("✅ [PERFIL DE TESTE] Produto de teste criado com sucesso!");
        };
    }
}
