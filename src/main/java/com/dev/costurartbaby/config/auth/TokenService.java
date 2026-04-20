package com.dev.costurartbaby.config.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.dev.costurartbaby.entities.cliente.ContaEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Serviço responsável por gerar e validar tokens JWT para autenticação.
 * Ele utiliza a biblioteca Auth0 Java JWT para criar e verificar os tokens.
 * O segredo para assinar os tokens é configurado no arquivo de propriedades da aplicação.
 */

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(ContaEntity conta) {
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("costurartbaby")
                    .withSubject(conta.getUsername())
                    .withExpiresAt(genexpiratationdate())
                    .sign(algorithm);
            return token;
        }catch(JWTCreationException e){
            throw new RuntimeException("Erro ao gerar token JWT", e);
        }
    }


    /** O método generateRefreshToken é responsável por gerar um token de atualização (refresh token) para um usuário autenticado.
     * O token de atualização tem um tempo de expiração mais longo (7 dias) em comparação com o token de acesso,
     * permitindo que o usuário obtenha um novo token de acesso sem precisar fazer login novamente.
     * O método retorna o token de atualização gerado ou lança uma exceção em caso de erro durante a criação do token.
     */

    public String generateRefreshToken(ContaEntity conta){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("costurartbaby")
                    .withSubject(conta.getUsername())
                    .withExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                    .sign(algorithm);
            return token;
        }catch(JWTCreationException e){
            throw new RuntimeException("Erro ao gerar token JWT", e);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("costurartbaby")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    private Instant genexpiratationdate() {
        return Instant.now().plus(2, ChronoUnit.HOURS);
    }
}
