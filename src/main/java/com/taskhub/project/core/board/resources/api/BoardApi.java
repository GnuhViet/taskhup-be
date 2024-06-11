package com.taskhub.project.core.board.resources.api;

import com.taskhub.project.common.Constants;
import com.taskhub.project.core.board.dto.BoardDto;
import com.taskhub.project.core.board.resources.api.model.*;
import com.taskhub.project.core.board.resources.api.model.boardCardApiModel.DeleteCommentReq;
import com.taskhub.project.core.board.service.BoardService;
import com.taskhub.project.common.service.model.ServiceResult;
import com.taskhub.project.core.board.service.BoardStarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/board")
@AllArgsConstructor
public class BoardApi {

    private final BoardService boardService;
    private final BoardStarService boardStarService;

    @Operation(summary = "Get all board", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ResponseEntity<ServiceResult<?>> getAllBoard() {
        var response = boardService.getAllBoard();
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "get single board", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{boardId}")
    public BoardDto getBoard(
            @PathVariable String boardId,
            Principal principal
    ) {
        return boardService.getBoard(boardId, principal.getName());
    }

    //TODO do validate role!!!!!
    @Operation(summary = "Create board", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<?> createBoard(@RequestBody BoardCreateReq boardDto, Principal principal) {
        var resp = boardService.createBoard(boardDto, principal.getName());
        return new ResponseEntity<>(resp, resp.getHttpStatus());
    }

    @Operation(summary = "Star a board", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/board-star")
    public ResponseEntity<?> starBoard(
            @RequestBody BoardStarReq request,
            Principal principal
    ) {
        var resp = boardService.starBoard(request, principal.getName());
        return new ResponseEntity<>(resp, resp.getHttpStatus());
    }

    @GetMapping("/info/{boardId}")
    @Operation(summary = "Get board info", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<?> getBoardInfo(
            @PathVariable String boardId,
            Principal principal
    ) {
        var resp = boardService.getBoardInfo(boardId);
        return new ResponseEntity<>(resp, resp.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_BOARD)
    @PostMapping("/update-board-info")
    @Operation(summary = "Get board info", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getBoardInfo(
            @RequestBody BoardInfoUpdateReq request,
            Principal principal
    ) {
        var resp = boardService.updateBoardInfo(request, principal.getName());
        return new ResponseEntity<>(resp, resp.getHttpStatus());
    }

    @Secured(Constants.ActionString.EDIT_BOARD)
    @PostMapping("/update-board-background")
    @Operation(summary = "Get board info", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getBoardInfo(
            @RequestBody BoardBgUpdateReq request,
            Principal principal
    ) {
        var resp = boardService.updateBoardBackground(request, principal.getName());
        return new ResponseEntity<>(resp, resp.getHttpStatus());
    }

    // delete card
    @Secured(Constants.ActionString.EDIT_BOARD)
    @PostMapping("/delete-board-card")
    @Operation(summary = "Get board info", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getBoardInfo(
            @RequestBody BoardCardDeleteReq request,
            Principal principal
    ) {
        var resp = boardService.deleteBoardCard(request, principal.getName());
        return new ResponseEntity<>(resp, resp.getHttpStatus());
    }

    // update colum title
    @Secured(Constants.ActionString.EDIT_BOARD)
    @PostMapping("/update-colum-title")
    @Operation(summary = "Get board info", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getBoardInfo(
            @RequestBody UpdateColumTitleReq request,
            Principal principal
    ) {
        var resp = boardService.updateColumTitle(request, principal.getName());
        return new ResponseEntity<>(resp, resp.getHttpStatus());
    }

    // detele collumn
    @Secured(Constants.ActionString.EDIT_BOARD)
    @PostMapping("/delete-colum")
    @Operation(summary = "Get board info", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getBoardInfo(
            @RequestBody DeleteCommentReq request,
            Principal principal
    ) {
        var resp = boardService.deleteColumn(request, principal.getName());
        return new ResponseEntity<>(resp, resp.getHttpStatus());
    }
}
