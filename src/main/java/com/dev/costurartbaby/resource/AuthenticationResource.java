package com.dev.costurartbaby.resource;


import com.dev.costurartbaby.config.auth.TokenService;
import com.dev.costurartbaby.entities.cliente.ContaEntity;
import com.dev.costurartbaby.entities.dto.AuthenticationDTO;
import com.dev.costurartbaby.entities.dto.ContaRequest;
import com.dev.costurartbaby.entities.dto.LoginResponse;
import com.dev.costurartbaby.service.ContaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * A classe AuthenticationResource é um controlador REST responsável por expor endpoints para autenticação e registro de usuários.
 * Ela utiliza a anotação @RestController para indicar que é um controlador REST e @RequestMapping para definir a rota base "/api/auth".
 * O método login é mapeado para o endpoint "/login" e recebe um objeto AuthenticationDTO contendo as credenciais do usuário (login e senha).
 * Ele utiliza o AuthenticationManager para autenticar o usuário e, se a autenticação for bem-sucedida, gera um token JWT usando o TokenService.
 * O token gerado é retornado em um objeto LoginResponse.
 * O método register é mapeado para o endpoint "/register" e recebe um objeto AuthenticationDTO contendo as credenciais do usuário para registro.
 * Ele chama o serviço ContaService para criar uma nova conta com as informações fornecidas e retorna uma resposta vazia com status 200 (OK) em caso de sucesso.
 */

@RestController
@RequestMapping("/api/auth")
public class AuthenticationResource {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final ContaService contaService;

    public AuthenticationResource(AuthenticationManager authenticationManager, TokenService tokenService, ContaService contaService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.contaService = contaService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid AuthenticationDTO data) {
        var userNamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.senha());
        var auth = this.authenticationManager.authenticate(userNamePassword);

        var token = tokenService.generateToken((ContaEntity) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponse(token));
    }

   @PostMapping("/register")
   public ResponseEntity<Void> register(@RequestBody @Valid AuthenticationDTO data) {
       contaService.createConta(new ContaRequest(data.login(), data.senha()));
       return ResponseEntity.ok().build();
   }
}
