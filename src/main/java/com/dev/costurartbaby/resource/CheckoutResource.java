package com.dev.costurartbaby.resource;

import com.dev.costurartbaby.entities.dto.CheckoutRequest;
import com.dev.costurartbaby.entities.dto.CheckoutResponse;
import com.dev.costurartbaby.service.CheckoutService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutResource {

    private final CheckoutService checkoutService;

    public CheckoutResource(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping("/payment-link")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CheckoutResponse> criarLinkPagamento(@RequestBody @Valid CheckoutRequest request) {
        return ResponseEntity.ok(checkoutService.criarLinkPagamento(request));
    }
}