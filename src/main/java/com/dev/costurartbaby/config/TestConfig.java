package com.dev.costurartbaby.config;

import com.dev.costurartbaby.entities.cliente.ContaEntity;
import com.dev.costurartbaby.entities.cliente.ContaRole;
import com.dev.costurartbaby.repository.ContaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

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
}