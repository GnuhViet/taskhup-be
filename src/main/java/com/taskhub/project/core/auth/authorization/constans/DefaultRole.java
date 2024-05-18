package com.taskhub.project.core.auth.authorization.constans;

import lombok.Getter;

@Getter
public enum DefaultRole {
    OWNER("default-role-owner"),
    GUEST("default-role-guest"),
    MEMBER("default-role-member");

    private final String id;

    DefaultRole(String role) {
        this.id = role;
    }

    public static boolean isDefaultRole(String id) {
        return id.equals(OWNER.id) || id.equals(GUEST.id) || id.equals(MEMBER.id);
    }
}
