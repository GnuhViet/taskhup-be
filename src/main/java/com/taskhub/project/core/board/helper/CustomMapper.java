package com.taskhub.project.core.board.helper;

import com.taskhub.project.common.Constants;
import com.taskhub.project.core.board.domain.Board;
import com.taskhub.project.core.board.domain.BoardCard;
import com.taskhub.project.core.board.domain.BoardColumn;
import com.taskhub.project.core.board.domain.CardCustomField;
import com.taskhub.project.core.board.dto.*;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
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

    public static CardCustomFieldDetail toCardCustomFieldDetail(CardCustomField ccf) {
        List<FieldOptions> optionsList = null;

        if (ccf.getType().equals(Constants.CustomFieldTypes.DROPDOWN)) {
            var options = ccf.getOptions().split(",");
            optionsList = Arrays.stream(options).map(option -> {
                var colorValue = option.split("-");
                return FieldOptions.builder()
                        .color(colorValue[0])
                        .title(colorValue[1])
                        .build();
            }).toList();
        }


        return CardCustomFieldDetail
                .builder()
                .id(ccf.getId())
                .type(ccf.getType())
                .options(optionsList)
                .title(ccf.getTitle())
                .createDate(ccf.getCreateDate())
                .templateId(ccf.getTemplateId())
                .build();
    }
}
