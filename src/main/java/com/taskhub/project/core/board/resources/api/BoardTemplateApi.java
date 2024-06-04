package com.taskhub.project.core.board.resources.api;

import com.taskhub.project.common.Constants;
import com.taskhub.project.common.service.model.ServiceResult;
import com.taskhub.project.core.board.resources.api.model.*;
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

    // api get template label
    @GetMapping("/labels/{templateId}")
    @Operation(summary = "Get all template", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> getLabel(@PathVariable String templateId) {
        var response = service.getLabel(templateId);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    // api create template label
    @Secured(Constants.ActionString.EDIT_CARD_TEMPLATE)
    @PostMapping("/labels/create")
    @Operation(summary = "Create new template", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> createLabel(
            @RequestBody CardLabelCreateReq request,
            Authentication authentication
    ) {
        var response = service.createLabel(request, String.valueOf(authentication.getCredentials()));
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    // api edit template label
    @Secured(Constants.ActionString.EDIT_CARD_TEMPLATE)
    @PostMapping("/labels/update")
    @Operation(summary = "Create new template", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> editLabel(
            @RequestBody CardLabelUpdateReq request,
            Authentication authentication
    ) {
        var response = service.editLabel(request, String.valueOf(authentication.getCredentials()));
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    // api delete template label
    @Secured(Constants.ActionString.EDIT_CARD_TEMPLATE)
    @PostMapping("/labels/delete")
    @Operation(summary = "Create new template", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> deleteLabel(
            @RequestBody CardLabelDeleteReq request,
            Authentication authentication
    ) {
        var response = service.deleteLabel(request, String.valueOf(authentication.getCredentials()));
        return new ResponseEntity<>(response, response.getHttpStatus());
    }


    // api get template custom field
    @GetMapping("/fields/{templateId}")
    @Operation(summary = "Get all template", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> getFields(@PathVariable String templateId) {
        var response = service.getFields(templateId);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    // api create template custom field
    @Secured(Constants.ActionString.EDIT_CARD_TEMPLATE)
    @PostMapping("/fields/create")
    @Operation(summary = "Create new template", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> createFields(
            @RequestBody CardCustomFieldCreateReq request,
            Authentication authentication
    ) {
        var response = service.createFields(request, String.valueOf(authentication.getCredentials()));
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    // api edit template label
    @Secured(Constants.ActionString.EDIT_CARD_TEMPLATE)
    @PostMapping("/fields/update")
    @Operation(summary = "Create new template", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> editFields(
            @RequestBody CardCustomFieldUpdateReq request,
            Authentication authentication
    ) {
        var response = service.editFields(request, String.valueOf(authentication.getCredentials()));
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    // api delete template label
    @Secured(Constants.ActionString.EDIT_CARD_TEMPLATE)
    @PostMapping("/fields/delete")
    @Operation(summary = "Create new template", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> deleteFields(
            @RequestBody CardCustomFieldDeleteReq request,
            Authentication authentication
    ) {
        var response = service.deleteFields(request, String.valueOf(authentication.getCredentials()));
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
