package com.taskhub.project.core.user.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_notification_read")
public class UserNotificationRead {
    @EmbeddedId
    private UserNotificationReadKey id;
}
