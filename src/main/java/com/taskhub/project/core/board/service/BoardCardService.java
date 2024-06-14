package com.taskhub.project.core.board.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskhub.project.aspect.exception.model.ErrorsData;
import com.taskhub.project.common.CommonFunction;
import com.taskhub.project.common.Constants;
import com.taskhub.project.common.service.model.ServiceResult;
import com.taskhub.project.core.board.domain.*;
import com.taskhub.project.core.board.dto.CardCustomFieldDetail;
import com.taskhub.project.core.board.helper.CustomMapper;
import com.taskhub.project.core.board.repo.*;
import com.taskhub.project.core.board.resources.api.model.boardCardApiModel.*;
import com.taskhub.project.core.board.resources.api.model.boardCardDetails.*;
import com.taskhub.project.core.file.domain.FileInfo;
import com.taskhub.project.core.file.impl.CloudinaryFileService;
import com.taskhub.project.core.helper.validator.ValidatorService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.taskhub.project.core.board.service.HistoryService.CardHistoryType;

@Service
@AllArgsConstructor
public class BoardCardService {
    private final BoardRepo boardRepo;
    private final BoardCardRepo boardCardRepo;
    private final BoardCardMemberRepo boardCardMemberRepo;
    private final BoardTemplateRepo boardTemplateRepo;
    private final BoardCardHistoryRepo boardCardHistoryRepo;
    private final CardLabelRepo cardLabelRepo;
    private final CardCustomFieldRepo cardCustomFieldRepo;
    private final BoardCardAttachmentsRepo boardCardAttachmentsRepo;
    private final BoardCardCommentsRepo boardCardCommentsRepo;
    private final BoardCardWatchRepo boardCardWatchRepo;
    private final BoardColumnRepo boardColumnRepo;

    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;

    private final HistoryService historyService;
    private final CloudinaryFileService fileService;
    private final ValidatorService validator;

    public static class AttachmentType {
        public static final String CARD_ATTACH = "CARD_ATTACH";
        public static final String COMMENT_ATTACH = "COMMENT_ATTACH";
    }

    public static DateTimeFormatter dateTimeFormater = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static DateTimeFormatter dateFormater = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ServiceResult<?> getCardDetails(String boardCardId, String userId) {
        var boardCardDb = new BoardCard.BoardCardDetail[1];

        validator.validate()
                .withConstraint(
                        () -> {
                            boardCardDb[0] = boardCardRepo.getCardDetails(boardCardId)
                                    .orElse(null);
                            return boardCardDb[0] == null;
                        },
                        ErrorsData.of("boardCardId", "04", "Board card not found")
                )
                .throwIfFails();
        var card = boardCardDb[0]; // BoardCardDetail


        var boardCardMembers = boardCardMemberRepo.findByCardId(boardCardId);
        var isWatchCard = boardCardWatchRepo.findByCardIdAndUserId(boardCardId, userId).isPresent();
        var cardCustomFields = cardCustomFieldRepo.findByTemplateIdOrderByCreateDate(card.getTemplateId());
        var allAttachments = boardCardAttachmentsRepo.findByCardId(boardCardId);
        var allComments = boardCardCommentsRepo.findByCardId(boardCardId);
        var top20ActivityHistory = historyService.getCardHistoryDetails(boardCardId, true);

        Map<String, List<BoardCardAttachments.BoardCardAttachmentsInfo>> attachmentMap = allAttachments.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(BoardCardAttachments.BoardCardAttachmentsInfo::getType));

        List<BoardCardAttachment> cardAttachments = null;
        if (attachmentMap.containsKey(AttachmentType.CARD_ATTACH)) {
            cardAttachments = attachmentMap.get(AttachmentType.CARD_ATTACH)
                    .stream()
                    .map(m -> mapper.map(m, BoardCardAttachment.class))
                    .toList();
        }

        List<BoardCardMemberSimple> members = null;
        if (!boardCardMembers.isEmpty()) {
            members = boardCardMembers.stream()
                    .map(m -> mapper.map(m, BoardCardMemberSimple.class))
                    .toList();
        }

        List<BoardCardSelectedLabel> selectedLabels = null;
        if (!StringUtils.isBlank(card.getSelectedLabelsIdRaw())) {
            List<CardLabel> boardCardLabel;
            boardCardLabel = cardLabelRepo.findByListId(
                    Arrays.stream(card.getSelectedLabelsIdRaw().split(",")).toList()
            );
            if (boardCardLabel != null) {
                selectedLabels = boardCardLabel.stream()
                        .map(l -> mapper.map(l, BoardCardSelectedLabel.class))
                        .toList();
            }
        }

        List<BoardCardCheckList> checkLists = null;
        if (!StringUtils.isBlank(card.getCheckListsRaw())) {
            try {
                checkLists = objectMapper.readValue(
                        card.getCheckListsRaw(),
                        new TypeReference<List<BoardCardCheckList>>(){}
                );
            } catch (Exception e) {
                return ServiceResult.error("INTERNAL_SERVER_ERROR");
            }
        }

        List<CardCustomFieldDetail> cardCustomFieldDetails = null;
        if (!cardCustomFields.isEmpty()) {
            cardCustomFieldDetails = cardCustomFields.stream()
                    .map(CustomMapper::toCardCustomFieldDetail)
                    .toList();
        }

        List<BoardCardSelectedFields> cardCustomFieldValue = null;
        if (!StringUtils.isBlank(card.getSelectedFieldsValueRaw())) {
            try {
                cardCustomFieldValue = objectMapper.readValue(
                        card.getSelectedFieldsValueRaw(),
                        new TypeReference<List<BoardCardSelectedFields>>(){}
                );
            } catch (Exception e) {
                return ServiceResult.error("INTERNAL_SERVER_ERROR");
            }
        }

        List<BoardCardComments> comments = null;
        Map<String ,List<BoardCardAttachments.BoardCardAttachmentsInfo>> mapAttachItemByCommentId;

        if (attachmentMap.containsKey(AttachmentType.COMMENT_ATTACH)) {
            mapAttachItemByCommentId = attachmentMap.get(AttachmentType.COMMENT_ATTACH)
                    .stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(BoardCardAttachments.BoardCardAttachmentsInfo::getRefId));
        } else {
            mapAttachItemByCommentId = new HashMap<>();
        }

        List<BoardCardComment> boardCardComments = null;
        if (!allComments.isEmpty()) {
            boardCardComments = allComments.stream()
                    .map(comment -> {
                        var commentAttachments = mapAttachItemByCommentId.get(comment.getId());
                        List<BoardCardAttachment> attachments = null;
                        if (commentAttachments != null) {
                            attachments = commentAttachments.stream()
                                    .map(m -> mapper.map(m, BoardCardAttachment.class))
                                    .toList();
                        }
                        return BoardCardComment.builder()
                                .id(comment.getId())
                                .content(comment.getContent())
                                .createAt(comment.getCreateAt())
                                .createBy(comment.getCreateBy())
                                .fullName(comment.getFullName())
                                .username(comment.getUsername())
                                .avatarUrl(comment.getAvatarUrl())
                                .editable(comment.getCreateBy().equals(userId))
                                .attachments(attachments)
                                .build();
                    })
                    .toList();
        }

        String fromDate = card.getFromDate() != null ? dateFormater.format(card.getFromDate()) : null;
        String deadlineDate = card.getDeadlineDate() != null ? dateTimeFormater.format(card.getDeadlineDate()) : null;

        return ServiceResult.ok(
                BoardCardDetails.builder()
                        .id(card.getId())
                        .templateId(card.getTemplateId())
                        .title(card.getTitle())
                        .columnId(card.getColumnId())
                        .columnName(card.getColumnName())
                        .coverUrl(card.getCoverUrl())
                        .members(members)
                        .selectedLabels(selectedLabels)
                        .isWatchCard(isWatchCard)
                        .fromDate(fromDate)
                        .deadlineDate(deadlineDate)
                        .workingStatus(card.getWorkingStatus())
                        .description(card.getDescription())
                        .checkLists(checkLists)
                        .customFields(cardCustomFieldDetails)
                        .selectedFieldsValue(cardCustomFieldValue)
                        .attachments(cardAttachments)
                        .comments(boardCardComments)
                        .activityHistory(top20ActivityHistory)
                        .build()
        );
    }

    public ServiceResult<?> getCardHistory(String boardCardId) {
        var allHistory = historyService.getCardHistoryDetails(boardCardId, false);
        return ServiceResult.ok(allHistory);
    }

    public ServiceResult<?> updateCardTitle(UpdateCardTitleRequest req, String userId) {
        var boardCardDb = new BoardCard[1];

        validator.tryValidate(req)
                .withConstraint(
                        () -> {
                            boardCardDb[0] = boardCardRepo.findById(req.getBoardCardId()).orElse(null);
                            return boardCardDb[0] == null;
                        },
                        ErrorsData.of("boardCardId", "04", "Board card not found")
                )
                .throwIfFails();

        var card = boardCardDb[0];

        card.setTitle(req.getTitle());

        boardCardRepo.save(card);

        historyService.createCardHistoryAsync(
                card.getId(),
                CardHistoryType.UPDATE_TITLE,
                card.getTitle(),
                req.getTitle(),
                userId
        );

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> selectTemplate(SelectTemplateRequest req, String userId) {
        var boardCardDb = new BoardCard[1];
        var templateDb = new BoardCardTemplate[1];

        validator.tryValidate(req)
                .withConstraint(
                        () -> {
                            boardCardDb[0] = boardCardRepo.findById(req.getBoardCardId()).orElse(null);
                            return boardCardDb[0] == null;
                        },
                        ErrorsData.of("boardCardId", "04", "Board card not found")
                )
                .withConstraint(
                        () -> {
                            templateDb[0] = boardTemplateRepo.findById(req.getTemplateId()).orElse(null);
                            return templateDb[0] == null;
                        },
                        ErrorsData.of("templateId", "04", "Template not found")
                )
                .throwIfFails();

        var boardCard = boardCardDb[0];
        var template = templateDb[0];

        historyService.createCardHistoryAsync(
                boardCard.getId(),
                CardHistoryType.SELECT_TEMPLATE,
                boardCard.getTemplateId(),
                template.getTitle(),
                userId
        );

        boardCard.setTemplateId(template.getId());
        boardCard.setCardLabelValues("[]");
        boardCard.setCustomFieldValue("[]");

        boardCardRepo.save(boardCard);

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }


    public ServiceResult<?> selectLabel(SelectCardLabelRequest req, String userId) {
        var boardCardDb = new BoardCard[1];

        validator.tryValidate(req)
                .withConstraint(
                        () -> {
                            boardCardDb[0] = boardCardRepo.findById(req.getBoardCardId()).orElse(null);
                            return boardCardDb[0] == null;
                        },
                        ErrorsData.of("boardCardId", "04", "Board card not found")
                )
                .withConstraint(
                        () -> !boardTemplateRepo.hasLabel(
                                boardCardDb[0].getTemplateId(),
                                req.getBoardCardLabelValue()
                        ),
                        ErrorsData.of("BoardCardLabelValue", "05", "Label not belong to template")
                )
                .throwIfFails();

        var card = boardCardDb[0];

        String oldLabel = card.getCardLabelValues();

        card.setCardLabelValues(
                String.join(
                       ",",
                        req.getBoardCardLabelValue()
                )
        );

        boardCardRepo.save(card);

        historyService.createCardHistoryAsync(
                card.getId(),
                CardHistoryType.SELECT_LABEL,
                oldLabel,
                card.getCardLabelValues(),
                userId
        );

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> selectField(SelectCardFieldRequest req, String userId) {
        var boardCardDb = new BoardCard[1];

        validator.tryValidate(req)
                .withConstraint(
                        () -> {
                            boardCardDb[0] = boardCardRepo.findById(req.getBoardCardId()).orElse(null);
                            return boardCardDb[0] == null;
                        },
                        ErrorsData.of("boardCardId", "04", "Board card not found")
                )
                .withConstraint(
                        () -> !boardTemplateRepo.hasField(
                                boardCardDb[0].getTemplateId(),
                                req.getCustomFieldValue() != null ?
                                        req.getCustomFieldValue().stream()
                                                .map(BoardCardSelectedFields::getFieldId)
                                                .collect(Collectors.toList())
                                        : null
                        ),
                        ErrorsData.of("BoardCardFieldValue", "05", "Field not belong to template")
                )
                .throwIfFails();

        var card = boardCardDb[0];

        String customFieldValue;
        try {
            var newCardCustomField = req.getCustomFieldValue();
            List<BoardCardSelectedFields> oldCardCustomField = null;
            if (!StringUtils.isBlank(card.getCustomFieldValue())) {
                try {
                    var oldString = card.getCustomFieldValue();
                    oldCardCustomField = objectMapper.readValue(
                            oldString,
                            new TypeReference<List<BoardCardSelectedFields>>(){}
                    );
                } catch (Exception e) {
                    return ServiceResult.error("INTERNAL_SERVER_ERROR");
                }
            }

            if (oldCardCustomField != null) {
                for (var item : newCardCustomField) {
                    item.setValue(
                            oldCardCustomField.stream()
                                    .filter(oldField -> oldField.getFieldId().equals(item.getFieldId()))
                                    .findFirst()
                                    .map(BoardCardSelectedFields::getValue)
                                    .orElse(null)
                    );
                }
            }

            customFieldValue = objectMapper.writeValueAsString(newCardCustomField);

        } catch (Exception e) {
            return ServiceResult.error("INTERNAL_SERVER_ERROR");
        }

        String oldLabel = card.getCustomFieldValue();

        card.setCustomFieldValue(customFieldValue);

        boardCardRepo.save(card);

        historyService.createCardHistoryAsync(
                card.getId(),
                CardHistoryType.SELECT_FIELD,
                oldLabel,
                customFieldValue,
                userId
        );

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> updateField(UpdateFieldValueRequest req, String userId) {
        var boardCardDb = new BoardCard[1];

        validator.tryValidate(req)
                .withConstraint(
                        () -> {
                            boardCardDb[0] = boardCardRepo.findById(req.getBoardCardId()).orElse(null);
                            return boardCardDb[0] == null;
                        },
                        ErrorsData.of("boardCardId", "04", "Board card not found")
                )
                .withConstraint(
                        () -> !boardTemplateRepo.hasField(
                                boardCardDb[0].getTemplateId(),
                                req.getCustomFieldValue() != null ?
                                        List.of(req.getCustomFieldValue().getFieldId())
                                        : null
                        ),
                        ErrorsData.of("BoardCardFieldValue", "05", "Field not belong to template")
                )
                .throwIfFails();

        var card = boardCardDb[0];

        String customFieldValue;
        try {
            List<BoardCardSelectedFields> oldCardCustomField = null;
            if (!StringUtils.isBlank(card.getCustomFieldValue())) {
                try {
                    var oldString = card.getCustomFieldValue();
                    oldCardCustomField = objectMapper.readValue(
                            oldString,
                            new TypeReference<List<BoardCardSelectedFields>>(){}
                    );
                } catch (Exception e) {
                    return ServiceResult.error("INTERNAL_SERVER_ERROR");
                }
            }

            if (oldCardCustomField == null) {
                customFieldValue = objectMapper.writeValueAsString(req.getCustomFieldValue());
            } else {

                for (var item : oldCardCustomField) {
                    if (item.getFieldId().equals(req.getCustomFieldValue().getFieldId())) {
                        item.setValue(req.getCustomFieldValue().getValue());
                        break;
                    }
                }

                customFieldValue = objectMapper.writeValueAsString(oldCardCustomField);
            }


        } catch (Exception e) {
            return ServiceResult.error("INTERNAL_SERVER_ERROR");
        }

        card.setCustomFieldValue(customFieldValue);

        boardCardRepo.save(card);

        historyService.createCardHistoryAsync(
                card.getId(),
                CardHistoryType.UPDATE_FIELD,
                null,
                req.getCustomFieldValue().getFieldId(),
                userId
        );

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> updateMember(UpdateMemberRequest req, String userId) {
        var boardCardDb = new BoardCard[1];

        validator.tryValidate(req)
                .withConstraint(
                        () -> {
                            boardCardDb[0] = boardCardRepo.findById(req.getBoardCardId()).orElse(null);
                            return boardCardDb[0] == null;
                        },
                        ErrorsData.of("boardCardId", "04", "Board card not found")
                )
                .throwIfFails();

        var card = boardCardDb[0];
        Map<String, BoardCardMember> oldMembersMap = boardCardMemberRepo
                .findObjByCardId(req.getBoardCardId())
                .stream()
                .collect(Collectors.toMap(member -> member.getId().getUserId(), Function.identity()));

        List<BoardCardMember> newMembersList = new LinkedList<>();
        List<BoardCardMember> removeMembersList = new LinkedList<>();

        for (var item : req.getMembers()) {
            if (oldMembersMap.containsKey(item)) {
                continue;
            }

            var newMember = BoardCardMember.builder()
                    .id(
                            new BoardCardMemberKey(
                                    item,
                                    req.getBoardCardId()
                            )
                    )
                    .build();

            newMembersList.add(newMember);
        }

        for (var item : oldMembersMap.keySet()) {
            if (!req.getMembers().contains(item)) {
                removeMembersList.add(oldMembersMap.get(item));
            }
        }

        boardCardMemberRepo.saveAll(newMembersList);
        boardCardMemberRepo.deleteAll(removeMembersList);

        historyService.createCardHistoryAsync(
                card.getId(),
                CardHistoryType.UPDATE_MEMBER,
                removeMembersList.stream()
                        .map(m -> m.getId().getUserId())
                        .collect(Collectors.joining(",")),
                newMembersList.stream()
                        .map(m -> m.getId().getUserId())
                        .collect(Collectors.joining(",")),
                userId
        );

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> updateCheckList(UpdateCheckListReq req, String userId) {
        var boardCardDb = new BoardCard[1];

        validator.tryValidate(req)
                .withConstraint(
                        () -> {
                            boardCardDb[0] = boardCardRepo.findById(req.getBoardCardId()).orElse(null);
                            return boardCardDb[0] == null;
                        },
                        ErrorsData.of("boardCardId", "04", "Board card not found")
                )
                .throwIfFails();

        var card = boardCardDb[0];

        List<BoardCardCheckList> oldCheckList = null;
        if (!StringUtils.isBlank(card.getCheckListValue())) {
            try {
                var oldString = card.getCheckListValue();
                oldCheckList = objectMapper.readValue(
                        oldString,
                        new TypeReference<List<BoardCardCheckList>>(){}
                );
            } catch (Exception e) {
                return ServiceResult.error("INTERNAL_SERVER_ERROR");
            }
        }

        Map<String, BoardCardCheckList> oldCheckListMap = null;

        if (oldCheckList != null) {
            oldCheckListMap = oldCheckList.stream()
                    .collect(Collectors.toMap(BoardCardCheckList::getId, Function.identity()));
        }

        for (var item : req.getCheckListValue()) {
            if (oldCheckListMap != null && oldCheckListMap.containsKey(item.getId())) {
                item.setChecked(oldCheckListMap.get(item.getId()).isChecked());
            }
        }

        String checkListString = null;
        try {
            checkListString = objectMapper.writeValueAsString(req.getCheckListValue());
        } catch (Exception e) {
            return ServiceResult.error("INTERNAL_SERVER_ERROR");
        }

        String oldCheckListString = card.getCheckListValue();

        card.setCheckListValue(checkListString);

        boardCardRepo.save(card);

        historyService.createCardHistoryAsync(
                card.getId(),
                CardHistoryType.UPDATE_CHECKLIST,
                oldCheckListString,
                checkListString,
                userId
        );

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> updateCheckListValue(UpdateCheckListValueReq req, String userId) {
        var boardCardDb = new BoardCard[1];

        validator.tryValidate(req)
                .withConstraint(
                        () -> {
                            boardCardDb[0] = boardCardRepo.findById(req.getBoardCardId()).orElse(null);
                            return boardCardDb[0] == null;
                        },
                        ErrorsData.of("boardCardId", "04", "Board card not found")
                )
                .throwIfFails();

        var card = boardCardDb[0];

        List<BoardCardCheckList> oldCheckList = null;
        if (!StringUtils.isBlank(card.getCheckListValue())) {
            try {
                var oldString = card.getCheckListValue();
                oldCheckList = objectMapper.readValue(
                        oldString,
                        new TypeReference<List<BoardCardCheckList>>(){}
                );
            } catch (Exception e) {
                return ServiceResult.error("INTERNAL_SERVER_ERROR");
            }
        }

        if (oldCheckList == null) {
            return ServiceResult.error("OLD_CHECK_LIST_NOT_FOUND");
        }

        for (var item : oldCheckList) {
            if (item.getId().equals(req.getId())) {
                item.setChecked(req.getChecked());
                break;
            }
        }

        String checkListString = null;
        try {
            checkListString = objectMapper.writeValueAsString(oldCheckList);
        } catch (Exception e) {
            return ServiceResult.error("INTERNAL_SERVER_ERROR");
        }

        String oldCheckListString = card.getCheckListValue();

        card.setCheckListValue(checkListString);

        boardCardRepo.save(card);

        historyService.createCardHistoryAsync(
                card.getId(),
                CardHistoryType.UPDATE_CHECKLIST_VALUE,
                oldCheckListString,
                checkListString,
                userId
        );

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> updateWatchCard(UpdateWatchCardReq req, String userId) {
        var boardWatchDB = new BoardCardWatch[1];
        validator.tryValidate(req)
                .withConstraint(
                        () -> {
                            if (req.getIsWatch()) return false;

                            boardWatchDB[0] = boardCardWatchRepo.findByCardIdAndUserId(
                                    req.getBoardCardId(),
                                    userId
                            ).orElse(null);
                            return !req.getIsWatch() &&
                                    boardWatchDB[0] == null;
                        },
                        ErrorsData.of("boardCardIdWatch", "04", "Board card watcher not found")
                )
                .throwIfFails();

        var watch = boardWatchDB[0];
        if (req.getIsWatch()) {
            if (watch == null) {
                watch = BoardCardWatch.builder()
                        .id(
                                new BoardCardMemberKey(
                                        userId,
                                        req.getBoardCardId()
                                )
                        )
                        .build();
                boardCardWatchRepo.save(watch);
            }
        } else {
            boardCardWatchRepo.delete(watch);
        }

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> updateCardCover(UpdateCardCoverReq req, String userId) {
        var boardCardDb = new BoardCard[1];

        validator.tryValidate(req)
                .withConstraint(
                        () -> {
                            boardCardDb[0] = boardCardRepo.findById(req.getBoardCardId()).orElse(null);
                            return boardCardDb[0] == null;
                        },
                        ErrorsData.of("boardCardId", "04", "Board card not found")
                )
                .withConstraint(
                        () -> !CommonFunction.isImage(req.getFile()),
                        ErrorsData.of("file", "05", "File not image")
                )
                .throwIfFails();

        var card = boardCardDb[0];

        var oldCover = card.getCover();

        if (!StringUtils.isBlank(oldCover)) {
            var resp = fileService.deleteFile(oldCover);
            if (!fileService.isDeleteSuccess(resp)) {
                return ServiceResult.error("Internal server error: Failed to delete old avatar");
            }
        }

        var fileInfo = fileService.uploadFile(req.getFile());

        if (!fileService.isUploadSuccess(fileInfo)) {
            return ServiceResult.error("Internal server error: Failed to upload avatar");
        }

        card.setCover(((FileInfo) fileInfo.getData()).getId());

        boardCardRepo.save(card);

        historyService.createCardHistoryAsync(
                card.getId(),
                CardHistoryType.UPDATE_COVER,
                null,
                null,
                userId
        );

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> removeCardCover(RemoveCoverRequest req, String userId) {
        var boardCardDb = new BoardCard[1];

        validator.tryValidate(req)
                .withConstraint(
                        () -> {
                            boardCardDb[0] = boardCardRepo.findById(req.getBoardCardId()).orElse(null);
                            return boardCardDb[0] == null;
                        },
                        ErrorsData.of("boardCardId", "04", "Board card not found")
                )
                .throwIfFails();

        var card = boardCardDb[0];

        var oldCover = card.getCover();

        if (!StringUtils.isBlank(oldCover)) {
            var resp = fileService.deleteFile(oldCover);
            if (!fileService.isDeleteSuccess(resp)) {
                return ServiceResult.error("Internal server error: Failed to delete old avatar");
            }
        }

        card.setCover(null);

        boardCardRepo.save(card);

        historyService.createCardHistoryAsync(
                card.getId(),
                CardHistoryType.REMOVE_COVER,
                null,
                null,
                userId
        );

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> updateCardDate(UpdateCardDateRequest req, String userId) {
        var boardCardDb = new BoardCard[1];

        validator.tryValidate(req)
                .withConstraint(
                        () -> {
                            boardCardDb[0] = boardCardRepo.findById(req.getBoardCardId()).orElse(null);
                            return boardCardDb[0] == null;
                        },
                        ErrorsData.of("boardCardId", "04", "Board card not found")
                )
                .throwIfFails();

        var card = boardCardDb[0];

        LocalDateTime fd = null;
        LocalDateTime dd = null;

        try {
            fd = LocalDate.parse(req.getFromDate(), dateFormater).atStartOfDay();
            if (req.getDeadlineDate().contains(":")) {
                dd = LocalDateTime.parse(req.getDeadlineDate(), dateTimeFormater);
            } else {
                dd = LocalDate.parse(req.getDeadlineDate(), dateFormater).atStartOfDay();
            }
        } catch (Exception e) {
            return ServiceResult.error("INTERNAL_SERVER_ERROR");
        }

        String oldFromDate = card.getFromDate() != null ? dateFormater.format(card.getFromDate()) : null;
        String oldDeadlineDate = card.getDeadlineDate() != null ? dateTimeFormater.format(card.getDeadlineDate()) : null;

        card.setFromDate(fd);
        card.setDeadlineDate(dd);
        card.setReminder(req.getReminder());

        boardCardRepo.save(card);

        historyService.createCardHistoryAsync(
                card.getId(),
                CardHistoryType.UPDATE_DATE,
                oldFromDate + " @ " + oldDeadlineDate,
                req.getFromDate() + " @ " + req.getDeadlineDate(),
                userId
        );

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> updateWorkingStatus(UpdateWorkingStatusReq req, String userId) {
        var boardCardDb = new BoardCard[1];

        validator.tryValidate(req)
                .withConstraint(
                        () -> {
                            boardCardDb[0] = boardCardRepo.findById(req.getBoardCardId()).orElse(null);
                            return boardCardDb[0] == null;
                        },
                        ErrorsData.of("boardCardId", "04", "Board card not found")
                )
                .throwIfFails();

        var card = boardCardDb[0];
        var column = card.getBoardColumn();
        var board = column.getBoard();

        var workingStatus = 1;
        if (board.getIsNeedReview()) {
            workingStatus = 2;
        }

        card.setWorkingStatus(
                req.getWorkingStatus() ? workingStatus : null
        );

        boardCardRepo.save(card);

        historyService.createCardHistoryAsync(
                card.getId(),
                CardHistoryType.UPDATE_WORKING_STATUS,
                req.getWorkingStatus() ? null : String.valueOf(workingStatus),
                req.getWorkingStatus() ? String.valueOf(workingStatus) : null,
                userId
        );

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> updateDescription(UpdateDescriptionReq req, String userId) {
        var boardCardDb = new BoardCard[1];

        validator.tryValidate(req)
                .withConstraint(
                        () -> {
                            boardCardDb[0] = boardCardRepo.findById(req.getBoardCardId()).orElse(null);
                            return boardCardDb[0] == null;
                        },
                        ErrorsData.of("boardCardId", "04", "Board card not found")
                )
                .throwIfFails();

        var card = boardCardDb[0];

        card.setDescription(req.getDescription());

        boardCardRepo.save(card);

        historyService.createCardHistoryAsync(
                card.getId(),
                CardHistoryType.UPDATE_DESCRIPTION,
                null,
                null,
                userId
        );

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }


    public ServiceResult<?> uploadAttachment(
            MultipartFile file,
            UploadAttachmentRequest req,
            String userId
    ) {
        var boardCardDb = new BoardCard[1];
        var commentDb = new BoardCardComments[1];

        validator.tryValidate(req)
                .withConstraint(
                        () -> file == null || file.isEmpty(),
                        ErrorsData.of("file", "05", "File is required")
                )
                .withConstraint(
                        () -> {
                            if (req.getType().equals(AttachmentType.CARD_ATTACH)) {
                                boardCardDb[0] = boardCardRepo.findById(req.getRefId()).orElse(null);
                                return boardCardDb[0] == null;
                            } else {
                                commentDb[0] = boardCardCommentsRepo.findById(req.getRefId()).orElse(null);
                                return commentDb[0] == null;
                            }
                        },
                        ErrorsData.of("boardCardId", "04", "Board card not found")
                )
                .throwIfFails();

        var fileInfo = fileService.uploadFile(file);

        if (!fileService.isUploadSuccess(fileInfo)) {
            return ServiceResult.error("Internal server error: Failed to upload avatar");
        }

        var attachment = BoardCardAttachments.builder()
                .type(req.getType())
                .refId(req.getRefId())
                .fileId(((FileInfo) fileInfo.getData()).getId())
                .uploadBy(userId)
                .uploadAt(LocalDateTime.now())
                .displayName(req.getDisplayName())
                .build();

        boardCardAttachmentsRepo.save(attachment);

        historyService.createCardHistoryAsync(
                req.getRefId(),
                req.getType().equals(AttachmentType.CARD_ATTACH)
                        ? CardHistoryType.UPLOAD_ATTACHMENT_CARD
                        : CardHistoryType.UPLOAD_ATTACHMENT_COMMENT
                ,
                null,
                ((FileInfo) fileInfo.getData()).getOriginFileName(),
                userId
        );

        // CompletableFuture<ServiceResult<?>> future = fileService.uploadFileAsync(file);
        //
        // future.thenAccept(result -> {
        //     if (!fileService.isUploadSuccess(result)) {
        //         // Handle error
        //     } else {
        //         FileInfo fileInfo = (FileInfo) result.getData();
        //         var attachment = BoardCardAttachments.builder()
        //                 .type(req.getType())
        //                 .refId(req.getRefId())
        //                 .fileId(fileInfo.getId())
        //                 .uploadBy(userId)
        //                 .uploadAt(LocalDateTime.now())
        //                 .displayName(req.getDisplayName())
        //                 .build();
        //
        //         boardCardAttachmentsRepo.save(attachment);
        //     }
        // });

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> deleteAttachment(DeleteAttachmentReq request, String userId) {
        var attachmentDB = new BoardCardAttachments[1];
        validator.tryValidate(request)
                .withConstraint(
                        () -> {
                            attachmentDB[0] = boardCardAttachmentsRepo.findById(request.getAttachmentId()).orElse(null);
                            return attachmentDB[0] == null;
                        },
                        ErrorsData.of("attachmentId", "04", "Attachment not found")
                )
                .throwIfFails();

        var attachment = attachmentDB[0];


        if (attachment.getType().equals(AttachmentType.COMMENT_ATTACH)) {
            if (!attachment.getUploadBy().equals(userId)) {
                return ServiceResult.error("INVALID_USER");
            }
        }

        var resp = fileService.deleteFile(attachment.getFileId());
        if (!fileService.isDeleteSuccess(resp)) {
            return ServiceResult.error("Internal server error: Failed to delete old avatar");
        }

        boardCardAttachmentsRepo.delete(attachment);

        historyService.createCardHistoryAsync(
                attachment.getRefId(),
                attachment.getType().equals(AttachmentType.CARD_ATTACH)
                        ? CardHistoryType.DELETE_ATTACHMENT_CARD
                        : CardHistoryType.DELETE_ATTACHMENT_COMMENT
                ,
                null,
                null,
                userId
        );

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> createComment(CreateCommentReq req, String userId) {
        var boardCardDb = new BoardCard[1];

        validator.tryValidate(req)
                .withConstraint(
                        () -> {
                            boardCardDb[0] = boardCardRepo.findById(req.getBoardCardId()).orElse(null);
                            return boardCardDb[0] == null;
                        },
                        ErrorsData.of("boardCardId", "04", "Board card not found")
                )
                .throwIfFails();

        var card = boardCardDb[0];

        var comment = BoardCardComments.builder()
                .boardCardId(req.getBoardCardId())
                .content(req.getContent())
                .createBy(userId)
                .createAt(LocalDateTime.now())
                .build();

        boardCardCommentsRepo.save(comment);

        historyService.createCardHistoryAsync(
                req.getBoardCardId(),
                CardHistoryType.CREATE_COMMENT,
                null,
                null,
                userId
        );

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> editCommentContent(EditCommentContentReq req, String userId) {
        var commentDB = new BoardCardComments[1];
        validator.tryValidate(req)
                .withConstraint(
                        () -> {
                            commentDB[0] = boardCardCommentsRepo.findById(req.getId()).orElse(null);
                            return commentDB[0] == null;
                        },
                        ErrorsData.of("commentId", "04", "Comment not found")
                )
                .throwIfFails();

        var comment = commentDB[0];

        if (!comment.getCreateBy().equals(userId)) {
            return ServiceResult.error("INVALID_USER");
        }

        comment.setContent(req.getContent());

        boardCardCommentsRepo.save(comment);

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> deleteComment(DeleteCommentReq req, String userId) {
        var commentDB = new BoardCardComments[1];
        validator.tryValidate(req)
                .withConstraint(
                        () -> {
                            commentDB[0] = boardCardCommentsRepo.findById(req.getId()).orElse(null);
                            return commentDB[0] == null;
                        },
                        ErrorsData.of("commentId", "04", "Comment not found")
                )
                .throwIfFails();

        var comment = commentDB[0];

        if (!comment.getCreateBy().equals(userId)) {
            return ServiceResult.error("INVALID_USER");
        }

        var attachments = boardCardAttachmentsRepo.getByCommentId(comment.getId());

        // xoa attachment
        for (var attachment : attachments) {
            var resp = fileService.deleteFile(attachment.getFileId());
            if (!fileService.isDeleteSuccess(resp)) {
                return ServiceResult.error("Internal server error: Failed to delete old avatar");
            }
        }

        historyService.createCardHistoryAsync(
                comment.getBoardCardId(),
                CardHistoryType.DELETE_COMMENT,
                null,
                null,
                userId
        );

        boardCardAttachmentsRepo.deleteAll(attachments);
        boardCardCommentsRepo.delete(comment);

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }
}
