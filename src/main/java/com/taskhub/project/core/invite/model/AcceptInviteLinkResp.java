package com.taskhub.project.core.invite.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Builder
@Data
@AllArgsConstructor
public class AcceptInviteLinkResp {
    private String type;
    private String destinationId;
}
