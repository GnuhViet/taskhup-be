package com.taskhub.project.core.user.repo;

import com.taskhub.project.core.user.entities.UserNotificationRead;
import com.taskhub.project.core.user.entities.UserNotificationReadKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserNotificationReadRepo extends JpaRepository<UserNotificationRead, UserNotificationReadKey> {

    @Query(value = """
        select * from user_notification_read unr
        where unr.history_id in :listId and unr.user_id = :userId
    """, nativeQuery = true)
    List<UserNotificationRead> findByListIdAndUserId(List<String> listId, String userId);
}
