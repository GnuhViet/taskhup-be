package com.taskhub.project.core.board.resources.api.model.boardCardDetails;

import com.taskhub.project.core.board.domain.CardCustomField;
import com.taskhub.project.core.board.dto.CardCustomFieldDetail;
import com.taskhub.project.core.file.domain.FileInfo;
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
    private List<BoardCardMemberSimple> members;
    private List<BoardCardSelectedLabel> selectedLabels;
    private Boolean isWatchCard;
    private LocalDateTime fromDate;
    private LocalDateTime deadlineDate;
    private Integer workingStatus;
    private String description;
    private List<BoardCardCheckList> checkLists;
    private List<CardCustomFieldDetail> customFields;
    private List<BoardCardSelectedFields> selectedFieldsValue;
    private List<FileInfo> attachments;
    private List<BoardCardComment> comments;
    private List<BoardCardHistory> activityHistory;
}
