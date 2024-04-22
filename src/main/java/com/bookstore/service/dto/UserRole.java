package com.bookstore.service.dto;

/**
 * Enum class representing the user roles.
 */
public enum UserRole {
    ADMIN("admin"),
    USER("user");

    final private String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getValue() {
        return role;
    }
}