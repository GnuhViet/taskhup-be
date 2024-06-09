package com.taskhub.project.core.board.resources.api.model.boardCardDetails;

import com.taskhub.project.core.board.dto.CardCustomFieldDetail;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BoardCardDetails {
    private String id;
    private String templateId;
    private String title;
    private String columnId;
    private String columnName;
    private String coverUrl;
    private List<BoardCardMemberSimple> members;
    private List<BoardCardSelectedLabel> selectedLabels;
    private Boolean isWatchCard;
    private String fromDate;
    private String deadlineDate;
    private Integer workingStatus;
    private String description;
    private List<BoardCardCheckList> checkLists;
    private List<CardCustomFieldDetail> customFields;
    private List<BoardCardSelectedFields> selectedFieldsValue;
    private List<BoardCardAttachment> attachments;
    private List<BoardCardComment> comments;
    private List<BoardCardHistory> activityHistory;
}
