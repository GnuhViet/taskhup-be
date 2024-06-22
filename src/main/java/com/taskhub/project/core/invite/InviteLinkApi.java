package com.taskhub.project.core.invite;

import com.taskhub.project.common.Constants;
import com.taskhub.project.core.invite.model.InviteLinkCreateReq;
import com.taskhub.project.core.invite.model.SendEmailInviteLinkReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/invite")
@AllArgsConstructor
public class InviteLinkApi {
    private final InviteLinkService inviteLinkService;

    @GetMapping("/join/{id}")
    @Operation(summary = "create join request", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> createJoinRequest(Principal principal, @PathVariable String id) {
        var resp = inviteLinkService.createJoinRequest(principal.getName(), id);
        return new ResponseEntity<>(resp, resp.getHttpStatus());
    }

    @Secured(Constants.ActionString.MANAGE_USER)
    @PostMapping("/create")
    @Operation(summary = "create invite link", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> createInvite(@RequestBody InviteLinkCreateReq req, Principal principal) {
        var resp = inviteLinkService.createInviteLink(principal.getName(), req);
        return new ResponseEntity<>(resp, resp.getHttpStatus());
    }

    @Secured(Constants.ActionString.MANAGE_USER)
    @GetMapping("/get-link/{destinationId}")
    @Operation(summary = "get invite link", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getInviteLink(Principal principal, @PathVariable String destinationId) {
        var resp = inviteLinkService.getInviteLink(principal.getName(), destinationId);
        return new ResponseEntity<>(resp, resp.getHttpStatus());
    }

    @Secured(Constants.ActionString.MANAGE_USER)
    @PostMapping("/send-email")
    @Operation(summary = "send invite link via email", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> sendEmailInvite(Principal principal, @RequestBody SendEmailInviteLinkReq req) {
        var resp = inviteLinkService.sendEmailInvite(principal.getName(), req);
        return new ResponseEntity<>(resp, resp.getHttpStatus());
    }
}
