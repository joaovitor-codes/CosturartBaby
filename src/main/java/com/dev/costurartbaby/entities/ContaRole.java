package com.dev.costurartbaby.entities;

public enum ContaRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private final String role;

    ContaRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
