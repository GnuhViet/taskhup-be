package com.taskhub.project.core.board.resources.api.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewRequest {
    private String id;
    private String boardCardId;
    private String boardCardName;
    private LocalDateTime createdAt;
    private String userId;
    private String userFullName;
    private String userAvatar;
    private String username;
}
