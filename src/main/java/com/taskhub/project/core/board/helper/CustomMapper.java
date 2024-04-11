package com.taskhub.project.core.board.helper;

import com.taskhub.project.core.board.domain.Board;
import com.taskhub.project.core.board.domain.BoardCard;
import com.taskhub.project.core.board.domain.BoardColumn;
import com.taskhub.project.core.board.dto.BoardCardDto;
import com.taskhub.project.core.board.dto.BoardColumnDto;
import com.taskhub.project.core.board.dto.BoardDto;
import org.modelmapper.ModelMapper;

import java.util.function.Function;

public class CustomMapper {
    private static final ModelMapper mapper = new ModelMapper();

    /**
     * Maps a Board entity to a BoardDto
     *   all its columns to columnDto
     *     and cards to cardDto.
     */
    public static BoardDto deepMapBoard(Board board) {
        var boardDto = mapper.map(board, BoardDto.class);

        Function<BoardCard, BoardCardDto> toCardDto = card -> {
            var cardDto = mapper.map(card, BoardCardDto.class);
            cardDto.setBoardId(board.getId());
            return cardDto;
        };
        Function<BoardColumn, BoardColumnDto> toColumnDto = (column -> {
            var columnDto = mapper.map(column, BoardColumnDto.class);
            columnDto.setCards(column.getBoardCards().stream().map(toCardDto).toList());
            return columnDto;
        });
        boardDto.setColumns(board.getColumns().stream().map(toColumnDto).toList());

        return boardDto;
    }
}
