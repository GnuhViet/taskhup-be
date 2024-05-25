package com.taskhub.project.core.invite.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendEmailInviteLinkReq {
    @NotBlank(message = "email is mandatory")
    private String email;
    private String content;
    @NotBlank(message = "Destination id is mandatory")
    private String destinationId;
}
