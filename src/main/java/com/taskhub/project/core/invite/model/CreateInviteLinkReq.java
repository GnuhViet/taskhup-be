package com.taskhub.project.core.invite.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateInviteLinkReq {
    @Pattern(regexp = "^(WORKSPACE|BOARD)$", message = "Type must be WORKSPACE or BOARD")
    @NotBlank(message = "Type is mandatory")
    private String type;
    @NotBlank(message = "Destination id is mandatory")
    private String destinationId;
}
