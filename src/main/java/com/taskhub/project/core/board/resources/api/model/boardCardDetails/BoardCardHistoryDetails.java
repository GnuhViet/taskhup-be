package com.taskhub.project.core.board.resources.api.model.boardCardDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardCardHistoryDetails {
    private String id;
    private String type;
    private String toData;
    private LocalDateTime actionDate;

    private String userId;
    private String userName;
    private String userFullName;
    private String userAvatar;
}
