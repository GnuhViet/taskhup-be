package com.taskhub.project.core.board.resources.api;

import com.taskhub.project.common.Constants;
import com.taskhub.project.core.board.dto.BoardDto;
import com.taskhub.project.core.board.resources.api.model.BoardBgUpdateReq;
import com.taskhub.project.core.board.resources.api.model.BoardCreateReq;
import com.taskhub.project.core.board.resources.api.model.BoardInfoUpdateReq;
import com.taskhub.project.core.board.service.BoardService;
import com.taskhub.project.common.service.model.ServiceResult;
import com.taskhub.project.core.board.service.BoardStarService;
import com.taskhub.project.core.file.FileInfoRepo;
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
    @PostMapping("/{boardId}/star")
    public ResponseEntity<?> starBoard(@PathVariable String boardId, Principal principal) {
        throw new UnsupportedOperationException();
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
}
