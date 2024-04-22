package com.taskhub.project.core.user.constans;

import lombok.Getter;

@Getter
public enum DefaultRole {
    OWNER("default-role-owner");

    private final String id;

    DefaultRole(String role) {
        this.id = role;
    }
}
