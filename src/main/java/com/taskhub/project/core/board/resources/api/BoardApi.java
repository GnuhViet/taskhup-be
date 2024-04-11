package com.taskhub.project.core.board.resources.api;

import com.taskhub.project.core.board.dto.BoardDto;
import com.taskhub.project.core.board.service.BoardService;
import com.taskhub.project.comon.service.model.ServiceResult;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/board")
@AllArgsConstructor
public class BoardApi {

    private final BoardService boardService;

    @GetMapping
    public ResponseEntity<ServiceResult<?>> getAllBoard() {
        var response = boardService.getAllBoard();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/{boardId}")
    public BoardDto getBoard(@PathVariable String boardId) {
        return boardService.getBoard(boardId);
    }

    @PostMapping
    public BoardDto createBoard(@RequestBody BoardDto boardDto) {
        return boardService.createBoard(boardDto);
    }
}
