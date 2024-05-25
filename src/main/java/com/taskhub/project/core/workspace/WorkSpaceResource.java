package com.taskhub.project.core.workspace;

import com.taskhub.project.common.Constants;
import com.taskhub.project.core.workspace.model.DisabledMemberRequest;
import com.taskhub.project.core.workspace.model.JoinRequestADRequest;
import com.taskhub.project.core.workspace.model.WorkSpaceCreateReq;
import com.taskhub.project.core.workspace.model.WorkSpaceUpdateInfoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/workspace")
@RequiredArgsConstructor
public class WorkSpaceResource {
    private final WorkSpaceService workSpaceService;

    @PostMapping
    @Operation(summary = "Create new workspace", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> createWorkSpace(@RequestBody WorkSpaceCreateReq workSpace, Principal principal) {
        var response = workSpaceService.createWorkSpace(workSpace, principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @GetMapping("/info")
    @Operation(summary = "Get workspace info", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getWorkSpaceInfo(Authentication authentication) {
        var response = workSpaceService.getWorkSpaceInfo(String.valueOf(authentication.getCredentials()));
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured({Constants.ActionString.EDIT_WORKSPACE})
    @PostMapping("/update-avatar")
    @Operation(summary = "Update workspace avatar", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> updateWorkSpaceAvatar(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        var response = workSpaceService.updateWorkSpaceAvatar(file, String.valueOf(authentication.getCredentials()));
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PostMapping("/update-info")
    public ResponseEntity<?> updateWorkSpaceInfo(
            @RequestBody WorkSpaceUpdateInfoRequest request,
            Authentication authentication
    ) {
        var response = workSpaceService.updateWorkSpaceInfo(request, String.valueOf(authentication.getCredentials()));
        return new ResponseEntity<>(response, response.getHttpStatus());
    }


    @GetMapping
    @Operation(summary = "Get user workspace", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getWorkSpace(Principal principal) {
        var response = workSpaceService.getWorkSpace(principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    // @Secured(Constants.ActionString.MANAGE_USER)
    @GetMapping("/members")
    @Operation(summary = "get workspace member", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getWorkSpaceMembers(Authentication authentication) {
        var response = workSpaceService.getWorkSpaceMembers(String.valueOf(authentication.getCredentials()));
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.MANAGE_USER)
    @GetMapping("/join-request")
    @Operation(summary = "get workspace member", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getWorkSpaceMemberWaiting(Authentication authentication) {
        var response = workSpaceService.getWorkSpaceMemberWaiting(String.valueOf(authentication.getCredentials()));
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.MANAGE_USER)
    @PostMapping("/join-request/accept")
    @Operation(summary = "get workspace member", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> acceptWorkspaceMember(@RequestBody JoinRequestADRequest req, Authentication authentication) {
        var response = workSpaceService.acceptWorkspaceMember(req.getUserIds(), String.valueOf(authentication.getCredentials()));
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.MANAGE_USER)
    @PostMapping("/join-request/deny")
    @Operation(summary = "get workspace member", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> denyWorkspaceMember(@RequestBody JoinRequestADRequest req, Authentication authentication) {
        var response = workSpaceService.denyWorkspaceMember(req.getUserIds(), String.valueOf(authentication.getCredentials()));
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.MANAGE_USER)
    @PostMapping("/disabled-member")
    @Operation(summary = "disabled workspace member", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> denyWorkspaceMember(@RequestBody DisabledMemberRequest req, Authentication authentication) {
        var response = workSpaceService.disableWorkspaceMember(req, String.valueOf(authentication.getCredentials()));
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
