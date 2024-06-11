package com.taskhub.project.core.board.service;

import com.taskhub.project.aspect.exception.model.ErrorsData;
import com.taskhub.project.common.Constants;
import com.taskhub.project.core.board.domain.*;
import com.taskhub.project.core.board.dto.BoardCardDto;
import com.taskhub.project.core.board.dto.BoardDto;
import com.taskhub.project.common.CommonFunction;
import com.taskhub.project.core.board.helper.CustomMapper;
import com.taskhub.project.core.board.repo.*;
import com.taskhub.project.core.board.resources.api.model.*;
import com.taskhub.project.core.board.resources.api.model.boardCardDetails.BoardCardSelectedLabel;
import com.taskhub.project.core.board.resources.websocket.model.BoardSocket.*;
import com.taskhub.project.common.service.model.ServiceResult;
import com.taskhub.project.core.file.FileInfoRepo;
import com.taskhub.project.core.helper.validator.ValidatorService;
import com.taskhub.project.core.workspace.WorkSpaceRepo;
import com.taskhub.project.core.workspace.domain.BoardStar;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Transactional
public class BoardService {
    private final BoardRepo boardRepo;
    private final BoardColumnRepo boardColumnRepo;
    private final BoardCardRepo boardCardRepo;
    private final BoardCardMemberRepo boardCardMemberRepo;
    private final CardLabelRepo cardLabelRepo;
    private final WorkSpaceRepo workSpaceRepo;
    private final BoardStarRepo boardStarRepo;
    private final FileInfoRepo fileInfoRepo;

    private final ValidatorService validator;

    private final ModelMapper mapper;

    private static final String BOARD_DEFAULT_COLOR = "#1976d2";

    public ServiceResult<BoardCreateResp> createBoard(BoardCreateReq req, String userId) {
        validator.tryValidate(req)
                .withConstraint(
                        () -> !workSpaceRepo.isWorkSpaceOwner(req.getWorkspaceId(), userId),
                        ErrorsData.of("workspaceId", "Role.Invalid", "You are not the owner of the workspace")
                )
                .throwIfFails();

        var board = Board.builder()
                .title(req.getTitle())
                .color(BOARD_DEFAULT_COLOR)
                .workspace(workSpaceRepo.getReferenceById(req.getWorkspaceId()))
                .build();

        return ServiceResult.ok(mapper.map(
                boardRepo.save(board),
                BoardCreateResp.class
        ));
    }

    public BoardDto getBoard(String boardId, String userId) {
        var board = boardRepo.findById(boardId).orElseThrow();
        var boardColumns = boardColumnRepo.findByBoardId(boardId);
        var boardCards = boardCardRepo.findByListColumnId(
                boardColumns.stream().map(BoardColumn::getId).toList(),
                userId
        );

        var mapCardLabels = new HashMap<String, List<CardLabel>>();
        if (!boardCards.isEmpty()) {
            boardCards.forEach(card -> {
                var rawLabels = card.getSelectedLabelsId();
                if (StringUtils.isBlank(rawLabels)) {
                    return;
                }
                List<CardLabel> boardCardLabel;
                boardCardLabel = cardLabelRepo.findByListId(
                        Arrays.stream(card.getSelectedLabelsId().split(",")).toList()
                );
                if (boardCardLabel != null) {
                    mapCardLabels.put(card.getId(), boardCardLabel);
                }
            });
        }

        var mapCardMembers = new HashMap<String, List<BoardCardMember.BoardCardMemberDetail>>();
        boardCards.forEach(card -> {
            mapCardMembers.put(card.getId(), boardCardMemberRepo.findByCardId(card.getId()));
        });

        var mapBoardCardsByColumnId = boardCards.stream()
                .collect(Collectors.groupingBy(BoardCard.BoardCardInfo::getColumnId));

        return CustomMapper.deepMapBoard(
                board,
                boardColumns,
                mapBoardCardsByColumnId,
                mapCardLabels,
                mapCardMembers
        );
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
                .CardLabelValues("[]")
                .CustomFieldValue("[]")
                .CheckListValue("[]")
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

            if (!CommonFunction.isTheSameList(Arrays.stream(column.getCardOrderIds().split(",")).toList(), req.getCardOrderIds())) {
                return ServiceResult.badRequest();
            }

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

        List<String> toColumCardOrderList = StringUtils.isBlank(toColumn.getCardOrderIds())
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

    public ServiceResult<?> getBoardInfo(String boardId) {
        var boardDb = new Board[1];
        validator.validate()
                .withConstraint(() -> {
                    boardDb[0] = boardRepo.findById(boardId).orElse(null);
                    return boardDb[0] == null;
                }, ErrorsData.of("boardId", "NotFound", "Board not found"))
                .throwIfFails();

        var board = boardDb[0];

        return ServiceResult.ok(mapper.map(board, BoardInfoResp.class));
    }

    public ServiceResult<?> updateBoardInfo(BoardInfoUpdateReq req, String userId) {
        var boardDb = new Board[1];
        validator.tryValidate(req)
                .withConstraint(() -> {
                    boardDb[0] = boardRepo.findById(req.getId()).orElse(null);
                    return boardDb[0] == null;
                }, ErrorsData.of("boardId", "NotFound", "Board not found"))
                .withConstraint(
                        () -> !boardRepo.isBoardMember(req.getId(), userId),
                        ErrorsData.of("userid", "Role.Invalid", "You are not the member of the board")
                )
                .throwIfFails();

        var board = boardDb[0];

        board.setTitle(req.getTitle());
        board.setShortDescription(req.getShortDescription());
        board.setDescription(req.getDescription());

        boardRepo.save(board);

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> updateBoardBackground(BoardBgUpdateReq req, String userId) {
        var boardDb = new Board[1];
        validator.tryValidate(req)
                .withConstraint(() -> {
                    boardDb[0] = boardRepo.findById(req.getId()).orElse(null);
                    return boardDb[0] == null;
                }, ErrorsData.of("boardId", "NotFound", "Board not found"))
                .withConstraint(
                        () -> !boardRepo.isBoardMember(req.getId(), userId),
                        ErrorsData.of("userid", "Role.Invalid", "You are not the member of the board")
                )
                .throwIfFails();

        var board = boardDb[0];

        if (StringUtils.isBlank(req.getColor())) {
            board.setColor(BOARD_DEFAULT_COLOR);
        } else {
            board.setColor(req.getColor());
        }

        boardRepo.save(board);

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
    }

    public ServiceResult<?> starBoard(BoardStarReq req, String userId) {
        var boardDb = new Board[1];
        var boardStarDb = new BoardStar[1];
        validator.tryValidate(req)
                .withConstraint(
                        () -> {
                            boardDb[0] = boardRepo.findById(req.getBoardId()).orElse(null);
                            return boardDb[0] == null;
                        },
                        ErrorsData.of("boardId", "NotFound", "Board not found")
                )
                .withConstraint(
                        () -> {
                            if (req.getIsStarred()) return false;
                            boardStarDb[0] = boardStarRepo.findByBoardIdAndUserId(req.getBoardId(), userId);
                            return boardStarDb[0] == null;
                        },
                        ErrorsData.of("boardId", "NotFound", "Board not found")
                )
                .throwIfFails();

        var board = boardDb[0];

        if (!req.getIsStarred()) {
            var boardStar = boardStarDb[0];
            boardStarRepo.delete(boardStar);
            return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);
        }

        boardStarRepo.save(BoardStar.builder()
                .boardId(board.getId())
                .userId(userId)
                .build()
        );

        return ServiceResult.ok(mapper.map(board, BoardInfoResp.class));
    }
}
