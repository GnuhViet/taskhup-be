package com.taskhub.project.core.board.resources.api;

import com.taskhub.project.common.Constants;
import com.taskhub.project.common.service.model.ServiceResult;
import com.taskhub.project.core.board.resources.api.model.BoardCardTemplateCreateRequest;
import com.taskhub.project.core.board.resources.api.model.boardCardApiModel.*;
import com.taskhub.project.core.board.service.BoardCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/board-card")
@AllArgsConstructor
public class BoardCardApi {
    private final BoardCardService service;

    @PostMapping("/card-details")
    @Operation(summary = "Get card details", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> getCardDetails(
            @RequestBody CardDetailRequest request,
            Principal principal
    ) {
        var response = service.getCardDetails(request.getBoardCardId(), principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/update-title")
    @Operation(summary = "Update card title", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> updateTitle(
            @RequestBody UpdateCardTitleRequest request
    ) {
        var response = service.updateCardTitle(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/select-template")
    @Operation(summary = "Create new template", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> selectTemplate(
            @RequestBody SelectTemplateRequest request
    ) {
        var response = service.selectTemplate(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/select-label")
    @Operation(summary = "Create new template", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> selectLabel(
            @RequestBody SelectCardLabelRequest request
    ) {
        var response = service.selectLabel(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/select-field")
    @Operation(summary = "Create new template", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> selectField(
            @RequestBody SelectCardFieldRequest request
    ) {
        var response = service.selectField(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/update-field-value")
    @Operation(summary = "Create new template", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> selectField(
            @RequestBody UpdateFieldValueRequest request
    ) {
        var response = service.updateField(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/update-members")
    @Operation(summary = "Create new template", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> updateMembers(
            @RequestBody UpdateMemberRequest request
    ) {
        var response = service.updateMember(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/update-watch")
    @Operation(summary = "Update watch", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> updateWatchCard(
            @RequestBody UpdateWatchCardReq request,
            Principal principal
    ) {
        var response = service.updateWatchCard(request, principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/update-checklist")
    @Operation(summary = "Create new template", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> updateChecklist(
            @RequestBody UpdateCheckListReq request
    ) {
        var response = service.updateCheckList(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/update-checklist-value")
    @Operation(summary = "Create new template", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> updateCheckListValue(
            @RequestBody UpdateCheckListValueReq request
    ) {
        var response = service.updateCheckListValue(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PostMapping("/update-cover")
    public ResponseEntity<?> updateCover(
            @RequestParam("file") MultipartFile file,
            @RequestParam("boardCardId") String boardCardId
    ) {
        var response = service.updateCardCover(UpdateCardCoverReq.builder()
                .boardCardId(boardCardId)
                .file(file)
                .build()
        );
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/update-card-date")
    @Operation(summary = "Create new template", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> updateCardDate(
            @RequestBody UpdateCardDateRequest request
    ) {
        var response = service.updateCardDate(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/update-working-status")
    @Operation(summary = "Create new template", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> updateWorkingStatus(
            @RequestBody UpdateWorkingStatusReq request
    ) {
        var response = service.updateWorkingStatus(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/update-description")
    @Operation(summary = "Create new template", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> updateDescription(
            @RequestBody UpdateDescriptionReq request
    ) {
        var response = service.updateDescription(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PostMapping("/upload-attachment")
    public ResponseEntity<?> uploadAttachment(
            @RequestParam("file") MultipartFile file,
            @RequestParam("displayName") String displayName,
            @RequestParam("type") String type,
            @RequestParam("refId") String refId,
            Principal principal
    ) {
        var response = service.uploadAttachment(
                file,
                UploadAttachmentRequest.builder()
                        .displayName(displayName)
                        .type(type)
                        .refId(refId)
                        .build()
                ,
                principal.getName()
        );
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PostMapping("/delete-upload-attachment")
    public ResponseEntity<?> deleteAttachment(@RequestBody DeleteAttachmentReq request, Principal principal) {
        var response = service.deleteAttachment(request, principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }


    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/create-comment")
    @Operation(summary = "Create new template", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> createComment(
            @RequestBody CreateCommentReq request,
            Principal principal
    ) {
        var response = service.createComment(request, principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/edit-comment-content")
    @Operation(summary = "Create new template", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> editCommentContent(
            @RequestBody EditCommentContentReq request,
            Principal principal
    ) {
        var response = service.editCommentContent(request, principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/delete-comment")
    @Operation(summary = "Create new template", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> deleteComment(
            @RequestBody DeleteCommentReq request,
            Principal principal
    ) {
        var response = service.deleteComment(request, principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
