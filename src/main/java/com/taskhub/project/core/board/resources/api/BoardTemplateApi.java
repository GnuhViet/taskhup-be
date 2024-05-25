package com.taskhub.project.core.board.resources.api;

import com.taskhub.project.common.Constants;
import com.taskhub.project.common.service.model.ServiceResult;
import com.taskhub.project.core.board.resources.api.model.BoardCardTemplateCreateRequest;
import com.taskhub.project.core.board.service.BoardTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/board-template")
@AllArgsConstructor
public class BoardTemplateApi {

    private final BoardTemplateService service;

    @GetMapping("{boardId}")
    @Operation(summary = "Get all template", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> getTemplate(@PathVariable String boardId) {
        var response = service.getTemplate(boardId);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD_TEMPLATE)
    @PostMapping("/create")
    @Operation(summary = "Create new template", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> createTemplate(
            @RequestBody BoardCardTemplateCreateRequest request,
            Authentication authentication
    ) {
        var response = service.createTemplate(request, String.valueOf(authentication.getCredentials()));
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD_TEMPLATE)
    @PostMapping("/delete/{templateId}")
    @Operation(summary = "Create new template", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> createTemplate(
            @PathVariable String templateId,
            Authentication authentication
    ) {
        var response = service.deleteTemplate(templateId, String.valueOf(authentication.getCredentials()));
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

}
