package com.taskhub.project.core.board.service;

import com.taskhub.project.aspect.exception.model.ErrorsData;
import com.taskhub.project.common.Constants;
import com.taskhub.project.common.service.model.ServiceResult;
import com.taskhub.project.core.board.domain.Board;
import com.taskhub.project.core.board.domain.BoardCardTemplate;
import com.taskhub.project.core.board.domain.CardCustomField;
import com.taskhub.project.core.board.domain.CardLabel;
import com.taskhub.project.core.board.helper.CustomMapper;
import com.taskhub.project.core.board.repo.BoardRepo;
import com.taskhub.project.core.board.repo.BoardTemplateRepo;
import com.taskhub.project.core.board.repo.CardCustomFieldRepo;
import com.taskhub.project.core.board.repo.CardLabelRepo;
import com.taskhub.project.core.board.resources.api.model.*;
import com.taskhub.project.core.helper.validator.ValidatorService;
import com.taskhub.project.core.workspace.WorkSpaceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.taskhub.project.common.Constants.CustomFieldTypes.*;

@Service
@RequiredArgsConstructor
public class BoardTemplateService {
    private final BoardTemplateRepo boardTemplateRepo;
    private final BoardRepo boardRepo;
    private final WorkSpaceRepo workSpaceRepo;
    private final CardLabelRepo cardLabelRepo;
    private final CardCustomFieldRepo cardCustomFieldRepo;
    private final ValidatorService validator;

    public ServiceResult<?> getTemplate(String boardId) {
        return ServiceResult.ok(boardTemplateRepo.getTemplate(boardId));
    }

    public ServiceResult<?> createTemplate(BoardCardTemplateCreateRequest request, String workspaceId) {
        var boardDb = new Board[1];
        validator.tryValidate(request)
                .withConstraint(
                        () -> !workSpaceRepo.haveBoard(workspaceId, request.getBoardId()),
                        ErrorsData.of("workspaceId", "05", workspaceId)
                )
                .withConstraint(
                        () -> {
                            boardDb[0] = boardRepo.findById(request.getBoardId()).orElse(null);
                            return boardDb[0] == null;
                        },
                        ErrorsData.of("boardId", "06", request.getBoardId())
                )
                .throwIfFails();

        var board = boardDb[0];

        var template = new BoardCardTemplate();
        template.setBoardId(board.getId());
        template.setTitle(request.getTitle());
        template.setCreateDate(LocalDateTime.now());

        boardTemplateRepo.save(template);

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> deleteTemplate(String templateId, String workspaceId) {
        var templateDB = new BoardCardTemplate[1];

        validator.tryValidate(templateId)
                .withConstraint(
                        () -> {
                            templateDB[0] = boardTemplateRepo.findById(templateId).orElse(null);
                            return templateDB[0] == null;
                        },
                        ErrorsData.of("templateId", "05", workspaceId)
                )
                .withConstraint(
                        () -> !boardTemplateRepo.isBelongToWorkSpace(templateId, workspaceId),
                        ErrorsData.of("templateId", "06", workspaceId)
                )
                .withConstraint(
                        () -> boardTemplateRepo.isUsed(templateId),
                        ErrorsData.of("templateId", "07", workspaceId)
                )
                .throwIfFails();

        var template = templateDB[0];
        boardTemplateRepo.delete(template);

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> getLabel(String templateId) {
        return ServiceResult.ok(cardLabelRepo.findByTemplateIdOrderByCreateDate(templateId));
    }

    public ServiceResult<?> createLabel(CardLabelCreateReq req, String workspaceId) {
        var templateDB = new BoardCardTemplate[1];

        validator.tryValidate(req)
                .withConstraint(
                        () -> {
                            templateDB[0] = boardTemplateRepo.findById(req.getTemplateId()).orElse(null);
                            return templateDB[0] == null;
                        },
                        ErrorsData.of("templateId", "05", workspaceId)
                )
                .withConstraint(
                        () -> !boardTemplateRepo.isBelongToWorkSpace(req.getTemplateId(), workspaceId),
                        ErrorsData.of("templateId", "06", workspaceId)
                )
                .withConstraint(
                        () -> boardTemplateRepo.isUsed(req.getTemplateId()),
                        ErrorsData.of("templateId", "07", workspaceId)
                )
                .throwIfFails();

        var template = templateDB[0];

        var label = CardLabel.builder()
                .title(req.getTitle())
                .colorCode(req.getColorCode())
                .createDate(LocalDateTime.now())
                .templateId(template.getId())
                .build();

        cardLabelRepo.save(label);

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> editLabel(CardLabelUpdateReq req, String workspaceId) {
        var labelDB = new CardLabel[1];

        validator.tryValidate(req)
                .withConstraint(
                        () -> boardTemplateRepo.findById(req.getTemplateId()).orElse(null) == null,
                        ErrorsData.of("templateId", "05", workspaceId)
                )
                .withConstraint(
                        () -> {
                            labelDB[0] = cardLabelRepo.findById(req.getId()).orElse(null);
                            return labelDB[0] == null;
                        },
                        ErrorsData.of("Id", "06", workspaceId)
                )
                .withConstraint(
                        () -> !boardTemplateRepo.isBelongToWorkSpace(req.getTemplateId(), workspaceId),
                        ErrorsData.of("templateId", "06", workspaceId)
                )
                .withConstraint(
                        () -> boardTemplateRepo.isUsed(req.getTemplateId()),
                        ErrorsData.of("templateId", "07", workspaceId)
                )
                .throwIfFails();

        var label = labelDB[0];

        label.setTitle(req.getTitle());
        label.setColorCode(req.getColorCode());

        cardLabelRepo.save(label);

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> deleteLabel(CardLabelDeleteReq req, String workspaceId) {
        var labelDB = new CardLabel[1];

        validator.tryValidate(req)
                .withConstraint(
                        () -> boardTemplateRepo.findById(req.getTemplateId()).orElse(null) == null,
                        ErrorsData.of("templateId", "05", workspaceId)
                )
                .withConstraint(
                        () -> {
                            labelDB[0] = cardLabelRepo.findById(req.getId()).orElse(null);
                            return labelDB[0] == null;
                        },
                        ErrorsData.of("Id", "06", workspaceId)
                )
                .withConstraint(
                        () -> !boardTemplateRepo.isBelongToWorkSpace(req.getTemplateId(), workspaceId),
                        ErrorsData.of("templateId", "06", workspaceId)
                )
                .withConstraint(
                        () -> boardTemplateRepo.isUsed(req.getTemplateId()),
                        ErrorsData.of("templateId", "07", workspaceId)
                )
                .throwIfFails();

        var label = labelDB[0];

        cardLabelRepo.delete(label);

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> getFields(String templateId) {
        var cardCustomFields = cardCustomFieldRepo.findByTemplateIdOrderByCreateDate(templateId);

        var cardCustomFieldDetails = cardCustomFields.stream()
                .map(CustomMapper::toCardCustomFieldDetail)
                .toList();

        return ServiceResult.ok(cardCustomFieldDetails);
    }

    public ServiceResult<?> createFields(CardCustomFieldCreateReq req, String workspaceId) {
        validator.tryValidate(req)
                .withConstraint(
                        () -> boardTemplateRepo.findById(req.getTemplateId()).orElse(null) == null,
                        ErrorsData.of("templateId", "05", workspaceId)
                )
                .withConstraint(
                        () -> !boardTemplateRepo.isBelongToWorkSpace(req.getTemplateId(), workspaceId),
                        ErrorsData.of("templateId", "06", workspaceId)
                )
                .withConstraint(
                        () -> boardTemplateRepo.isUsed(req.getTemplateId()),
                        ErrorsData.of("templateId", "07", workspaceId)
                )
                .throwIfFails();

        var options = req.getOption();
        String optionsList = null;
        if (req.getType().equals(DROPDOWN) && options != null) {
            optionsList = options.stream()
                    .map(option ->
                            option.getColor() +
                            "-" +
                            option.getTitle()
                                .replace("-", "")
                                .replace(",", "")
                    )
                    .reduce((a, b) -> a + "," + b)
                    .orElse(null);
        }

        var cardCustomField = CardCustomField.builder()
                .type(req.getType())
                .options(optionsList)
                .title(req.getTitle())
                .createDate(LocalDateTime.now())
                .templateId(req.getTemplateId())
                .build();

        cardCustomFieldRepo.save(cardCustomField);

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> editFields(CardCustomFieldUpdateReq req, String workspaceId) {
        var cardCustomFieldDB = new CardCustomField[1];

        validator.tryValidate(req)
                .withConstraint(
                        () -> boardTemplateRepo.findById(req.getTemplateId()).orElse(null) == null,
                        ErrorsData.of("templateId", "05", workspaceId)
                )
                .withConstraint(
                        () -> {
                            cardCustomFieldDB[0] = cardCustomFieldRepo.findById(req.getId()).orElse(null);
                            return cardCustomFieldDB[0] == null;
                        },
                        ErrorsData.of("Id", "06", workspaceId)
                )
                .withConstraint(
                        () -> !boardTemplateRepo.isBelongToWorkSpace(req.getTemplateId(), workspaceId),
                        ErrorsData.of("templateId", "06", workspaceId)
                )
                .withConstraint(
                        () -> boardTemplateRepo.isUsed(req.getTemplateId()),
                        ErrorsData.of("templateId", "07", workspaceId)
                )
                .throwIfFails();

        var cardCustomField = cardCustomFieldDB[0];

        var options = req.getOption();
        String optionsList = null;
        if (req.getType().equals(DROPDOWN) && options != null) {
            optionsList = options.stream()
                    .map(option ->
                            option.getColor() +
                                    "-" +
                                    option.getTitle()
                                            .replace("-", "")
                                            .replace(",", "")
                    )
                    .reduce((a, b) -> a + "," + b)
                    .orElse(null);
        }

        cardCustomField.setType(req.getType());
        cardCustomField.setOptions(optionsList);
        cardCustomField.setTitle(req.getTitle());

        cardCustomFieldRepo.save(cardCustomField);

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> deleteFields(CardCustomFieldDeleteReq req, String workspaceId) {
        var cardCustomFieldDB = new CardCustomField[1];

        validator.tryValidate(req)
                .withConstraint(
                        () -> boardTemplateRepo.findById(req.getTemplateId()).orElse(null) == null,
                        ErrorsData.of("templateId", "05", workspaceId)
                )
                .withConstraint(
                        () -> {
                            cardCustomFieldDB[0] = cardCustomFieldRepo.findById(req.getId()).orElse(null);
                            return cardCustomFieldDB[0] == null;
                        },
                        ErrorsData.of("Id", "06", workspaceId)
                )
                .withConstraint(
                        () -> !boardTemplateRepo.isBelongToWorkSpace(req.getTemplateId(), workspaceId),
                        ErrorsData.of("templateId", "06", workspaceId)
                )
                .withConstraint(
                        () -> boardTemplateRepo.isUsed(req.getTemplateId()),
                        ErrorsData.of("templateId", "07", workspaceId)
                )
                .throwIfFails();

        var cardCustomField = cardCustomFieldDB[0];

        cardCustomFieldRepo.delete(cardCustomField);

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }
}
