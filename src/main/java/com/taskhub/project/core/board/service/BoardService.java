package com.taskhub.project.core.board.service;

import com.taskhub.project.aspect.exception.model.ErrorsData;
import com.taskhub.project.core.board.domain.Board;
import com.taskhub.project.core.board.domain.BoardCard;
import com.taskhub.project.core.board.domain.BoardColumn;
import com.taskhub.project.core.board.dto.BoardCardDto;
import com.taskhub.project.core.board.dto.BoardDto;
import com.taskhub.project.common.CommonFunction;
import com.taskhub.project.core.board.helper.CustomMapper;
import com.taskhub.project.core.board.repo.BoardCardRepo;
import com.taskhub.project.core.board.repo.BoardColumnRepo;
import com.taskhub.project.core.board.repo.BoardRepo;
import com.taskhub.project.core.board.resources.api.model.BoardCreateReq;
import com.taskhub.project.core.board.resources.api.model.BoardCreateResp;
import com.taskhub.project.core.board.resources.api.model.GetAllBoardResp;
import com.taskhub.project.core.board.resources.websocket.model.BoardSocket.*;
import com.taskhub.project.common.service.model.ServiceResult;
import com.taskhub.project.core.helper.validator.ValidatorService;
import com.taskhub.project.core.workspace.WorkSpaceRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Transactional
public class BoardService {
    private final BoardRepo boardRepo;
    private final BoardColumnRepo boardColumnRepo;
    private final BoardCardRepo boardCardRepo;
    private final WorkSpaceRepo workSpaceRepo;
    private final ValidatorService validator;
    private final ModelMapper mapper;

    public ServiceResult<BoardCreateResp> createBoard(BoardCreateReq req, String userId) {
        validator.tryValidate(req)
                .withConstraint(
                        () -> !workSpaceRepo.isWorkSpaceOwner(req.getWorkspaceId(), userId),
                        ErrorsData.of("workspaceId", "Role.Invalid", "You are not the owner of the workspace")
                )
                .throwIfFails();

        var board = Board.builder()
                .title(req.getTitle())
                .description("null") //TODO
                .workspace(workSpaceRepo.getReferenceById(req.getWorkspaceId()))
                .build();

        return ServiceResult.ok(mapper.map(
                boardRepo.save(board),
                BoardCreateResp.class
        ));
    }

    public BoardDto getBoard(String boardId) {
        var board = boardRepo.findById(boardId).orElseThrow();
        return CustomMapper.deepMapBoard(board);
    }

    public ServiceResult<List<GetAllBoardResp>> getAllBoard() {
        return ServiceResult.ok(
                boardRepo.findAll().stream()
                        .map(board -> mapper.map(board, GetAllBoardResp.class))
                        .toList()
        );
    }

    public ServiceResult<BoardColumnCreateResp> createColumn(String boardId, BoardColumnCreateReq req) {
        var board = boardRepo.findById(boardId).orElseThrow(() -> new RuntimeException("Board not found"));

        var column = boardColumnRepo.save(mapper.map(req, BoardColumn.class));

        column.setBoard(board);

        if (board.getColumnOrderIds() == null) {
            board.setColumnOrderIds(column.getId());
        } else {
            board.setColumnOrderIds(board.getColumnOrderIds() + "," + column.getId());
        }

        var resp = mapper.map(column, BoardColumnCreateResp.class);
        resp.setBoardId(boardId);

        return ServiceResult.created(resp);
    }

    public ServiceResult<BoardCardCreateResp> createCard(String boardId, BoardCardCreateReq req) {
        var column = boardColumnRepo.findById(req.getBoardColumnId())
                .orElseThrow(() -> new RuntimeException("Column not found"));

        var card = BoardCard.builder()
                .title(req.getTitle())
                .boardColumn(column)
                .build();

        var savedCard = boardCardRepo.save(card);
        if (column.getCardOrderIds() == null) {
            column.setCardOrderIds(savedCard.getId());
        } else {
            column.setCardOrderIds(column.getCardOrderIds() + "," + savedCard.getId());
        }

        boardCardRepo.save(savedCard);
        boardColumnRepo.save(column);

        var resp = mapper.map(savedCard, BoardCardCreateResp.class);
        resp.setColumnId(column.getId());
        resp.setBoardId(column.getBoard().getId());

        return ServiceResult.created(resp);
    }


    public ServiceResult<BoardColumnMoveReq> moveColumn(String boardId, BoardColumnMoveReq req) {
        if (req.getColumnOrderIds() == null || req.getColumnOrderIds().isEmpty()) {
            return ServiceResult.badRequest();
        }

        var board = boardRepo.findById(boardId).orElseThrow(() -> new RuntimeException("Board not found"));

        if (board.getColumns() == null || board.getColumnOrderIds() == null) {
            return ServiceResult.badRequest();
        }


        // TODO use common function
        var oldColumnOrderSet = Set.of(board.getColumnOrderIds().split(","));
        var newColumnOrderSet = new HashSet<>(req.getColumnOrderIds());

        if (!oldColumnOrderSet.equals(newColumnOrderSet)) {
            return ServiceResult.badRequest();
        }


        board.setColumnOrderIds(String.join(",", req.getColumnOrderIds()));

        boardRepo.save(board);

        return ServiceResult.ok(req);
    }

    public ServiceResult<BoardCardMoveReq> moveCard(String boardId, BoardCardMoveReq req) {
        // if (1==1) {
        //     throw new RuntimeException("Not implemented");
        // }

        var board = boardRepo.findById(boardId).orElseThrow(() -> new RuntimeException("Board not found"));

        var isMoveToSameColumn = req.getFromColumnId().equals(req.getToColumnId());

        if (isMoveToSameColumn) {
            var column = boardColumnRepo.findById(req.getFromColumnId()).orElseThrow(() -> new RuntimeException("Column not found"));
            column.setCardOrderIds(String.join(",", req.getCardOrderIds()));
            return ServiceResult.ok(req);
        }

        /*
         * if from column does not contain the card
         *  - return bad request
         * if to column card list is not the same as the request column card list (removed the new card)
         *  - return bad request
         * if to column order is not the same as the request column order (removed the new card)
         *  - return bad request
         *
         * delete from old column
         */

        var fromColumn = boardColumnRepo.findById(req.getFromColumnId()).orElseThrow(() -> new RuntimeException("Column not found"));
        var toColumn = boardColumnRepo.findById(req.getToColumnId()).orElseThrow(() -> new RuntimeException("Column not found"));
        var card = boardCardRepo.findById(req.getCardId()).orElseThrow(() -> new RuntimeException("Card not found"));

        var toColumCardOrderList = StringUtils.isBlank(toColumn.getCardOrderIds())
                ? List.of()
                : Arrays.asList(toColumn.getCardOrderIds().split(","));
        var requestCardOrderList = req.getCardOrderIds().stream().filter(cardId -> !req.getCardId().equals(cardId)).toList();

        if (!fromColumn.getCardOrderIds().contains(req.getCardId())) {
            return ServiceResult.badRequest();
        }

        if (!CommonFunction.isTheSameList(toColumCardOrderList, requestCardOrderList)) {
            return ServiceResult.badRequest();
        }

        if (!CommonFunction.isSameOrder(toColumCardOrderList, requestCardOrderList)) {
            return ServiceResult.badRequest();
        }

        var fromColumnOldCardOrder = fromColumn.getCardOrderIds().split(",");

        var fromColumnNewCardOrder = Arrays.stream(fromColumnOldCardOrder)
                .filter(cardId -> !req.getCardId().equals(cardId))
                .collect(Collectors.joining(","));
        var toColumnNewCardOrder = String.join(",", req.getCardOrderIds());

        fromColumn.setCardOrderIds(fromColumnNewCardOrder);
        toColumn.setCardOrderIds(toColumnNewCardOrder);
        card.setBoardColumn(toColumn);

        req.setCard(mapper.map(card, BoardCardDto.class));
        return ServiceResult.ok(req);
    }
}
