package com.taskhub.project.core.invite.model;

import lombok.Data;

@Data
public class CreateInviteLinkReq {
    private String type;
    private String destinationId;
}
