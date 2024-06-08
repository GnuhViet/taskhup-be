package com.taskhub.project.core.board.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskhub.project.aspect.exception.model.ErrorsData;
import com.taskhub.project.common.Constants;
import com.taskhub.project.common.service.model.ServiceResult;
import com.taskhub.project.core.board.domain.*;
import com.taskhub.project.core.board.helper.CustomMapper;
import com.taskhub.project.core.board.repo.*;
import com.taskhub.project.core.board.resources.api.model.boardCardApiModel.*;
import com.taskhub.project.core.board.resources.api.model.boardCardDetails.*;
import com.taskhub.project.core.helper.validator.ValidatorService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.taskhub.project.core.board.service.CardHistoryService.CardHistoryType;

@Service
@AllArgsConstructor
public class BoardCardService {
    private final BoardRepo boardRepo;
    private final BoardCardRepo boardCardRepo;
    private final BoardCardMemberRepo boardCardMemberRepo;
    private final BoardTemplateRepo boardTemplateRepo;
    private final CardHistoryService cardHistoryService;
    private final BoardCardHistoryRepo boardCardHistoryRepo;
    private final CardLabelRepo cardLabelRepo;
    private final CardCustomFieldRepo cardCustomFieldRepo;
    private final BoardCardAttachmentsRepo boardCardAttachmentsRepo;
    private final BoardCardCommentsRepo boardCardCommentsRepo;
    private final BoardCardWatchRepo boardCardWatchRepo;
    private final BoardColumnRepo boardColumnRepo;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;

    private final ValidatorService validator;

    public static class AttachmentType {
        public static final String CARD_ATTACH = "CARD_ATTACH";
        public static final String COMMENT_ATTACH = "COMMENT_ATTACH";
    }

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
        var allActivityHistory = boardCardHistoryRepo.findByCardId(boardCardId);


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

        var cardCustomFieldDetails = cardCustomFields.stream()
                .map(CustomMapper::toCardCustomFieldDetail)
                .toList();

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

        return ServiceResult.ok(
                BoardCardDetails.builder()
                        .id(card.getId())
                        .templateId(card.getTemplateId())
                        .title(card.getTitle())
                        .columnId(card.getColumnId())
                        .columnName(card.getColumnName())
                        .members(members)
                        .selectedLabels(selectedLabels)
                        .isWatchCard(isWatchCard)
                        .fromDate(card.getFromDate())
                        .deadlineDate(card.getDeadlineDate())
                        .workingStatus(card.getWorkingStatus())
                        .description(card.getDescription())
                        .checkLists(checkLists)
                        .customFields(cardCustomFieldDetails)
                        .selectedFieldsValue(cardCustomFieldValue)
                        .build()
        );
    }

    public ServiceResult<?> updateCardTitle(UpdateCardTitleRequest req) {
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

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> selectTemplate(SelectTemplateRequest req) {
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

        boardCard.setTemplateId(template.getId());
        boardCard.setCardLabelValues(null);
        boardCard.setCustomFieldValue(null);

        cardHistoryService.createHistory(
                boardCard.getId(),
                CardHistoryType.SELECT_TEMPLATE,
                boardCard.getTemplateId(),
                req.getTemplateId()
        );

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }


    public ServiceResult<?> selectLabel(SelectCardLabelRequest req) {
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

        card.setCardLabelValues(
                String.join(
                       ",",
                        req.getBoardCardLabelValue()
                )
        );
        boardCardRepo.save(card);

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> selectField(SelectCardFieldRequest req) {
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
                        ErrorsData.of("BoardCardLabelValue", "05", "Label not belong to template")
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

        card.setCustomFieldValue(customFieldValue);

        boardCardRepo.save(card);

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> updateField(UpdateFieldValueRequest req) {
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
                        ErrorsData.of("BoardCardLabelValue", "05", "Label not belong to template")
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

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> updateMember(UpdateMemberRequest req) {
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

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> updateCheckList(UpdateCheckListReq req) {
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

        card.setCheckListValue(checkListString);

        boardCardRepo.save(card);

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> updateCheckListValue(UpdateCheckListValueReq req) {
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

        card.setCheckListValue(checkListString);

        boardCardRepo.save(card);

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
}
