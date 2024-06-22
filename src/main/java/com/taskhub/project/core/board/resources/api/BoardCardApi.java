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

    @PostMapping("/card-history")
    @Operation(summary = "Get card history", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> getCardHistory(
            @RequestBody CardDetailRequest request
    ) {
        var response = service.getCardHistory(request.getBoardCardId());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/update-title")
    @Operation(summary = "Update card title", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> updateTitle(
            @RequestBody UpdateCardTitleRequest request,
            Principal principal
    ) {
        var response = service.updateCardTitle(request, principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/select-template")
    @Operation(summary = "select card template", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> selectTemplate(
            @RequestBody SelectTemplateRequest request,
            Principal principal
    ) {
        var response = service.selectTemplate(request, principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/select-label")
    @Operation(summary = "select card label", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> selectLabel(
            @RequestBody SelectCardLabelRequest request,
            Principal principal
    ) {
        var response = service.selectLabel(request, principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/select-field")
    @Operation(summary = "select card field", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> selectField(
            @RequestBody SelectCardFieldRequest request,
            Principal principal
    ) {
        var response = service.selectField(request, principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/update-field-value")
    @Operation(summary = "update select field value", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> updateFieldValue(
            @RequestBody UpdateFieldValueRequest request,
            Principal principal
    ) {
        var response = service.updateField(request, principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/update-members")
    @Operation(summary = "update card member", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> updateMembers(
            @RequestBody UpdateMemberRequest request,
            Principal principal
    ) {
        var response = service.updateMember(request, principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    // @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/update-watch")
    @Operation(summary = "Update card watch", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> updateWatchCard(
            @RequestBody UpdateWatchCardReq request,
            Principal principal
    ) {
        var response = service.updateWatchCard(request, principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/update-checklist")
    @Operation(summary = "update card checklist", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> updateChecklist(
            @RequestBody UpdateCheckListReq request,
            Principal principal
    ) {
        var response = service.updateCheckList(request, principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/update-checklist-value")
    @Operation(summary = "update card checklist value", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> updateCheckListValue(
            @RequestBody UpdateCheckListValueReq request,
            Principal principal
    ) {
        var response = service.updateCheckListValue(request, principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/update-cover")
    @Operation(summary = "update card cover", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> updateCover(
            @RequestParam("file") MultipartFile file,
            @RequestParam("boardCardId") String boardCardId,
            Principal principal
    ) {
        var response = service.updateCardCover(UpdateCardCoverReq.builder()
                .boardCardId(boardCardId)
                .file(file)
                .build(),
                principal.getName()
        );
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/remove-cover")
    @Operation(summary = "remove card cover", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> updateCover(
            @RequestBody RemoveCoverRequest request,
            Principal principal
    ) {
        var response = service.removeCardCover(request, principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/update-card-date")
    @Operation(summary = "update card date", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> updateCardDate(
            @RequestBody UpdateCardDateRequest request,
            Principal principal
    ) {
        var response = service.updateCardDate(request, principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/update-working-status")
    @Operation(summary = "update card working status", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> updateWorkingStatus(
            @RequestBody UpdateWorkingStatusReq request,
            Principal principal
    ) {
        var response = service.updateWorkingStatus(request, principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/update-description")
    @Operation(summary = "update card description", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> updateDescription(
            @RequestBody UpdateDescriptionReq request,
            Principal principal
    ) {
        var response = service.updateDescription(request, principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD)
    @Operation(summary = "update card/comment attachment", security = @SecurityRequirement(name = "bearerAuth"))
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

    @Secured(Constants.ActionString.EDIT_CARD)
    @Operation(summary = "delete card/comment attachment", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/delete-upload-attachment")
    public ResponseEntity<?> deleteAttachment(@RequestBody DeleteAttachmentReq request, Principal principal) {
        var response = service.deleteAttachment(request, principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }


    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/create-comment")
    @Operation(summary = "create card comment", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> createComment(
            @RequestBody CreateCommentReq request,
            Principal principal
    ) {
        var response = service.createComment(request, principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/edit-comment-content")
    @Operation(summary = "edit card comment content", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> editCommentContent(
            @RequestBody EditCommentContentReq request,
            Principal principal
    ) {
        var response = service.editCommentContent(request, principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_CARD)
    @PostMapping("/delete-comment")
    @Operation(summary = "delete card comment", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResult<?>> deleteComment(
            @RequestBody DeleteCommentReq request,
            Principal principal
    ) {
        var response = service.deleteComment(request, principal.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
