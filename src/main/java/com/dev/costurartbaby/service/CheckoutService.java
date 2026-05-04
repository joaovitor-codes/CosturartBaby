package com.dev.costurartbaby.service;

import com.dev.costurartbaby.entities.dto.CheckoutRequest;
import com.dev.costurartbaby.entities.dto.CheckoutResponse;

public interface CheckoutService {
    CheckoutResponse criarLinkPagamento(CheckoutRequest request);
}