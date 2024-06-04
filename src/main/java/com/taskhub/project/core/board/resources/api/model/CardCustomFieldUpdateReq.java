package com.taskhub.project.core.board.resources.api.model;

import com.taskhub.project.core.board.dto.FieldOptions;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class CardCustomFieldUpdateReq {
    @NotBlank(message = "id is required")
    private String id;
    @NotBlank(message = "type is required")
    @Pattern(regexp = "^(TEXT|DATE|CHECKBOX|DROPDOWN)$", message = "Type must be TEXT, DATE, CHECKBOX, or DROPDOWN")
    private String type;
    @NotNull(message = "options is required")
    private List<FieldOptions> option;
    @NotBlank(message = "title is required")
    private String title;
    @NotBlank(message = "templateId is required")
    private String templateId;
}
