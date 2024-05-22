package com.taskhub.project.core.auth.authorization.constans;

import lombok.Getter;

@Getter
public enum DefaultFile {
    AVATAR("default-user-avatar");

    private final String id;

    DefaultFile(String id) {
        this.id = id;
    }

    public static boolean isDefaultFile(String id) {
        return id.equals(AVATAR.id);
    }
}
