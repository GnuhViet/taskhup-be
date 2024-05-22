package com.taskhub.project.core.user;

import com.taskhub.project.core.auth.authentication.dtos.UpdateInfoRequest;
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
    public ResponseEntity<?> getUserInfo(Principal principal) {
        var response = appUserService.getUserInfo(principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @GetMapping("/email-info")
    public ResponseEntity<?> getUserEmailInfo(Principal principal) {
        var response = appUserService.getUserEmailInfo(principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PostMapping("/update-info")
    public ResponseEntity<?> updateUserInfo(Principal principal, @RequestBody UpdateInfoRequest request) {
        var response = appUserService.updateUserInfo(principal.getName(), request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PostMapping("/update-avatar")
    public ResponseEntity<?> updateAvatar(
            @RequestParam("file") MultipartFile file,
            Principal principal
    ) {
        var response = appUserService.updateAvatar(principal.getName(), file);
        return ResponseEntity.ok(response);
    }
}
