package com.taskhub.project.core.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class NotificationResp {
    private String id;
    private String boardCardId;
    private String boardCardName;
    private String type;
    private String toData;
    private LocalDateTime actionDate;
    private Boolean isRead;
    private String userId;
    private String userName;
    private String userFullName;
    private String userAvatar;
}
