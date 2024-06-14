package com.taskhub.project.core.user.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationReq {
    @NotNull(message = "isOnlyUnread is required")
    private Boolean isOnlyUnread;
}
