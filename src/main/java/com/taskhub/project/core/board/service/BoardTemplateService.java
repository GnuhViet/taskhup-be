package com.taskhub.project.core.board.service;

import com.taskhub.project.aspect.exception.model.ErrorsData;
import com.taskhub.project.common.Constants;
import com.taskhub.project.common.service.model.ServiceResult;
import com.taskhub.project.core.board.domain.Board;
import com.taskhub.project.core.board.domain.BoardCardTemplate;
import com.taskhub.project.core.board.repo.BoardRepo;
import com.taskhub.project.core.board.repo.BoardTemplateRepo;
import com.taskhub.project.core.board.resources.api.model.BoardCardTemplateCreateRequest;
import com.taskhub.project.core.helper.validator.ValidatorService;
import com.taskhub.project.core.workspace.WorkSpaceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BoardTemplateService {
    private final BoardTemplateRepo boardTemplateRepo;
    private final BoardRepo boardRepo;
    private final WorkSpaceRepo workSpaceRepo;
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
}
