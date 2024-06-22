package com.taskhub.project.core.auth.authentication;

import com.taskhub.project.core.auth.authentication.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthResource {
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "User register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request, Principal principal) {
        var result = authService.changePassword(request, principal.getName());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    @Operation(summary = "Login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/author-workspace")
    @Operation(summary = "Author board")
    public ResponseEntity<?> authorWorkspace(@RequestBody AuthorRequest request) {
        var response = authService.authorWorkspace(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh jwt token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/email/validate")
    @Operation(summary = "Send email validation token", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> emailConfirm(Principal principal) {
        authService.sendConfirmToken(principal.getName());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/email/confirm")
    @Operation(summary = "Email validation", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> validateEmailToken(@RequestBody ConfirmTokenRequest req, Principal principal) {
        var result = authService.validateEmailToken(req.getToken(), principal.getName());
        return new ResponseEntity<>(result, result.getHttpStatus());
    }
}
