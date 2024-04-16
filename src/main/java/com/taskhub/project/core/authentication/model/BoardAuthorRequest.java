package com.taskhub.project.core.authentication.model;

import lombok.Data;

@Data
public class BoardAuthorRequest {
    private String accessToken;
    private String boardId;
}
