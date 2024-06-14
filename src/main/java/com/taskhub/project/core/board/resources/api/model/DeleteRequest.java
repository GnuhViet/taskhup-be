package com.taskhub.project.core.board.resources.api.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeleteRequest {
    private String id;
    private String refId;
    private String refName;
    private LocalDateTime createdAt;
    private String type;
    private String userId;
    private String userFullName;
    private String userAvatar;
    private String username;
}
