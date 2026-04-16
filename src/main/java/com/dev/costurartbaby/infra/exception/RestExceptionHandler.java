package com.dev.costurartbaby.infra.exception;

import com.dev.costurartbaby.entities.dto.ErroResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<ErroResponse> handleValidacao(ValidacaoException ex, HttpServletRequest request) {
        var erro = new ErroResponse(
                System.currentTimeMillis(),
                HttpStatus.BAD_REQUEST.value(),
                "Regra de Negócio Violada",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(erro);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErroResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroResponse(
                System.currentTimeMillis(),
                HttpStatus.NOT_FOUND.value(),
                "Recurso não encontrado",
                ex.getMessage(),
                request.getRequestURI()
        ));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErroResponse> handleBusiness(BusinessException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErroResponse(
                System.currentTimeMillis(),
                HttpStatus.BAD_REQUEST.value(),
                "Violação de regra de negócio",
                ex.getMessage(),
                request.getRequestURI()
        ));
    }
}
