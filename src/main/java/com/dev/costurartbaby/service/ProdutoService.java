package com.dev.costurartbaby.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface ProdutoService {
    void processarWebHookYampi(JsonNode payLoad);
}
