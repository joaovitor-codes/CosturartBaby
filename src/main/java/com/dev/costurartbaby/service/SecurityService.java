package com.dev.costurartbaby.service;

import com.dev.costurartbaby.entities.ContaEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SecurityService {
    public boolean isContaOwner(Authentication authentication, UUID id) {
        if (authentication == null || !authentication.isAuthenticated() || id == null || !(authentication.getPrincipal() instanceof ContaEntity user)) {
            return false;
        }
        return user.getId().equals(id);
    }
}
