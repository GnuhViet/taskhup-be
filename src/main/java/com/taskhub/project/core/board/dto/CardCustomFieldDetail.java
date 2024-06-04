package com.taskhub.project.core.board.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CardCustomFieldDetail {
    private String id;
    private String type;
    private List<FieldOptions> options;
    private String title;
    private LocalDateTime createDate;
    private String templateId;
}
