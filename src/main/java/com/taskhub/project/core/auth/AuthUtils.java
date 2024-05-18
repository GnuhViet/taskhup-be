package com.taskhub.project.core.auth;

import org.springframework.security.core.Authentication;

import java.security.Principal;

public class AuthUtils {
    public static String getUserId(Principal principal) {
        return principal.getName();
    }

    public static String getUserId(Authentication authentication) {
        return authentication.getName();
    }

    public static String getWorkSpaceId(Authentication authentication) {
        return String.valueOf(authentication.getCredentials());
    }
}
