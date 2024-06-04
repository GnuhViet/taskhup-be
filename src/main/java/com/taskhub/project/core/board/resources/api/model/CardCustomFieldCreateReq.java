package com.taskhub.project.core.board.resources.api.model;

import com.taskhub.project.core.board.dto.FieldOptions;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class CardCustomFieldCreateReq {
    @NotBlank(message = "type is required")
    @Pattern(regexp = "^(TEXT|DATE|CHECKBOX|DROPDOWN)$", message = "Type must be TEXT, DATE, CHECKBOX, or DROPDOWN")
    private String type;
    private List<FieldOptions> option;
    @NotBlank(message = "title is required")
    private String title;
    @NotBlank(message = "templateId is required")
    private String templateId;
}
