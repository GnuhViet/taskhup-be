package com.taskhub.project.core.auth.authorization;

import com.taskhub.project.common.Constants;
import com.taskhub.project.core.auth.authorization.model.RoleAddMemberReq;
import com.taskhub.project.core.auth.authorization.model.RoleCreateReq;
import com.taskhub.project.core.auth.authorization.model.RoleUpdateReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/role")
@AllArgsConstructor
public class RoleApi {
    private final RoleService roleService;

    @Secured(Constants.ActionString.EDIT_ROLE)
    @PostMapping("/create")
    @Operation(summary = "Create a role", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> createRole(@RequestBody RoleCreateReq req, Authentication auth) {
        var resp = roleService.createRole(req, auth.getName(), String.valueOf(auth.getCredentials()));
        return new ResponseEntity<>(resp, resp.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_ROLE)
    @PostMapping("/update")
    @Operation(summary = "Update role actions", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> updateRole(@RequestBody RoleUpdateReq req, Authentication auth) {
        var resp = roleService.updateRole(req, auth.getName());
        return new ResponseEntity<>(resp, resp.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_ROLE)
    @PostMapping("/add-member")
    @Operation(summary = "Add member to role", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> addMember(@RequestBody RoleAddMemberReq req, Authentication auth) {
        var resp = roleService.addMember(req, auth.getName());
        return new ResponseEntity<>(resp, resp.getHttpStatus());
    }

    @GetMapping
    @Operation(summary = "Get ws role", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getRole(Authentication authentication) {
        var resp = roleService.getRole(String.valueOf(authentication.getCredentials()));
        return new ResponseEntity<>(resp, resp.getHttpStatus());
    }
}
