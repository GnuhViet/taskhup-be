package com.taskhub.project.core.invite;

import com.taskhub.project.comon.service.model.ServiceResult;
import com.taskhub.project.core.invite.model.CreateInviteLinkReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/invite")
@AllArgsConstructor
public class InviteLinkApi {
    private final InviteLinkService inviteLinkService;

    @GetMapping("/{id}")
    @Operation(summary = "create invite link", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> acceptInvite(Principal principal, @PathVariable String id) {
        var resp = inviteLinkService.acceptInvite(principal.getName(), id);
        return new ResponseEntity<>(resp, resp.getHttpStatus());
    }

    @PostMapping("/create")
    @Operation(summary = "create invite link", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> createInvite(@RequestBody CreateInviteLinkReq req, Principal principal) {
        var resp = inviteLinkService.createInviteLink(principal.getName(), req);
        return new ResponseEntity<>(resp, resp.getHttpStatus());
    }
}
