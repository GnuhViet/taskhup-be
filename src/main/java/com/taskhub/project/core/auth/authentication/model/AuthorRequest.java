package com.taskhub.project.core.auth.authentication.model;

import lombok.Data;

@Data
public class AuthorRequest {
    private String accessToken;
    private String workspaceId;
}
