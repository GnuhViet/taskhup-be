package com.taskhub.project.core.board.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FieldOptions {
    private String color;
    private String title;
}
