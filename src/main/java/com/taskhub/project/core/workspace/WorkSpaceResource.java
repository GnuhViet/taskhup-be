package com.taskhub.project.core.workspace;

import com.taskhub.project.core.workspace.model.WorkSpaceCreateReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


    @GetMapping
    @Operation(summary = "Get user workspace", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getWorkSpace(Principal principal) {
        var response = workSpaceService.getWorkSpace(principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
