package com.taskhub.project.core.board.dto;

import com.taskhub.project.core.board.resources.api.model.boardCardDetails.BoardCardMemberSimple;
import com.taskhub.project.core.board.resources.api.model.boardCardDetails.BoardCardSelectedLabel;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BoardCardDto {
    private String id;
    private String title;
    private String cover;
    private LocalDateTime fromDate;
    private LocalDateTime deadlineDate;
    private Integer workingStatus;
    private Integer isWatchCard;
    private Integer commentCount;
    private Integer attachmentCount;
    private String checkListsItems;
    List<BoardCardSelectedLabel> selectedLabels;
    List<BoardCardMemberSimple> members;

    private String boardId;
    private String columnId;
}
