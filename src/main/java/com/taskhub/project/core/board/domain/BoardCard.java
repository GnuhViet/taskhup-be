package com.taskhub.project.core.board.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "board_card")
public class BoardCard {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String cover; // link to bang file_info

    private LocalDateTime createAt;

    private String createBy;

    private LocalDateTime fromDate;

    private LocalDateTime deadlineDate;

    private Integer reminder;

    private Integer workingStatus;

    @Column(columnDefinition = "TEXT")
    private String CardLabelValues = "[]";

    // map json
    // fieldId -> value
    // 1 -> true,false,true,true ( dropdown )
    // 2 -> 3 (dropdown-option-value-order-selecter)
    @Column(columnDefinition = "TEXT")
    private String CustomFieldValue = "[]";

    @Column(columnDefinition = "TEXT")
    private String CheckListValue = "[]";

    private String templateId;

    @ManyToOne
    @JoinColumn(name = "board_column_id")
    private BoardColumn boardColumn;

    public interface BoardCardInfo {
        String getId();
        String getTitle();
        String getCover();
        String getSelectedLabelsId();
        LocalDateTime getFromDate();
        LocalDateTime getDeadlineDate();
        Integer getWorkingStatus();
        Integer getIsWatchCard();
        Integer getCommentCount();
        Integer getAttachmentCount();
        String getCheckListsItems();
        String getColumnId();
    }

    public interface BoardCardDetail {
        String getId();
        String getTemplateId();
        String getTitle();
        String getColumnId();
        String getColumnName();
        String getCoverUrl();
        String getSelectedLabelsIdRaw();
        LocalDateTime getFromDate();
        LocalDateTime getDeadlineDate();
        Integer getWorkingStatus();
        String getDescription();
        String getCheckListsRaw();
        String getSelectedFieldsValueRaw();
    }
}
