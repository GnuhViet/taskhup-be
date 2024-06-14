package com.taskhub.project.core.user.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class MarkAsReadNotificationReq {
    List<String> notificationIds;
}
