package com.taskhub.project.core.user;

import com.taskhub.project.core.auth.authentication.dtos.UpdateInfoRequest;
import com.taskhub.project.core.user.model.MarkAsReadNotificationReq;
import com.taskhub.project.core.user.model.NotificationReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserApi {
    private final AppUserService appUserService;

    @GetMapping("/info")
    @Operation(summary = "get user info", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getUserInfo(Principal principal) {
        var response = appUserService.getUserInfo(principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @GetMapping("/email-info")
    @Operation(summary = "get user email verify info", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getUserEmailInfo(Principal principal) {
        var response = appUserService.getUserEmailInfo(principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PostMapping("/update-info")
    @Operation(summary = "update user info", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> updateUserInfo(Principal principal, @RequestBody UpdateInfoRequest request) {
        var response = appUserService.updateUserInfo(principal.getName(), request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PostMapping("/update-avatar")
    @Operation(summary = "update user avatar", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> updateAvatar(
            @RequestParam("file") MultipartFile file,
            Principal principal
    ) {
        var response = appUserService.updateAvatar(principal.getName(), file);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/notifications")
    @Operation(summary = "get user notification", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getNotifications(
            @RequestBody NotificationReq request,
            Principal principal
    ) {
        var response = appUserService.getNotifications(request, principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PostMapping("/mark-as-read")
    @Operation(summary = "mark read notifications", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getNotifications(
            @RequestBody MarkAsReadNotificationReq request,
            Principal principal
    ) {
        var response = appUserService.markAsReadNotification(request, principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
