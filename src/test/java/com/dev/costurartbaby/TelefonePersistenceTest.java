package com.dev.costurartbaby;

import com.dev.costurartbaby.entities.cliente.ClienteEntity;
import com.dev.costurartbaby.entities.cliente.TelefoneEntity;
import com.dev.costurartbaby.repository.ClienteRepository;
import com.dev.costurartbaby.repository.TelefoneRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class TelefonePersistenceTest {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TelefoneRepository telefoneRepository;

    @Test
    @Transactional
    void devePersistirTelefonesDoCliente() {
        TelefoneEntity telefone = TelefoneEntity.builder()
                .numero("11999990000")
                .build();

        ClienteEntity cliente = ClienteEntity.builder()
                .nome("Cliente Teste")
                .cpf("39053344705")
                .dataNascimento(LocalDate.of(1995, 1, 1))
                .telefones(List.of(telefone))
                .build();

        telefone.setCliente(cliente);

        ClienteEntity saved = clienteRepository.save(cliente);

        assertTrue(clienteRepository.findByCpf("39053344705").isPresent());
        assertEquals(1, telefoneRepository.count());

        ClienteEntity loaded = clienteRepository.findById(saved.getId()).orElseThrow();
        assertEquals(1, loaded.getTelefones().size());
        assertEquals("11999990000", loaded.getTelefones().get(0).getNumero());
    }
}
