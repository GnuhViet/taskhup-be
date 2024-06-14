package com.taskhub.project.core.board.helper;

import com.taskhub.project.common.Constants;
import com.taskhub.project.core.board.domain.*;
import com.taskhub.project.core.board.dto.*;
import com.taskhub.project.core.board.resources.api.model.boardCardDetails.BoardCardMemberSimple;
import com.taskhub.project.core.board.resources.api.model.boardCardDetails.BoardCardSelectedLabel;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CustomMapper {
    private static final ModelMapper mapper = new ModelMapper();

    /**
     * Maps a Board entity to a BoardDto
     *   all its columns to columnDto
     *     and cards to cardDto.
     */
    public static BoardDto deepMapBoard(
            Board board,
            List<BoardColumn> columns,
            Map<String, List<BoardCard.BoardCardInfo>> cards,
            Map<String, List<CardLabel>> cardLabels,
            Map<String, List<BoardCardMember.BoardCardMemberDetail>> cardMembers
    ) {
        var boardDto = mapper.map(board, BoardDto.class);

        Function<BoardCard.BoardCardInfo, BoardCardDto> toCardDto = card -> {
            var cardDto = mapper.map(card, BoardCardDto.class);

            cardDto.setBoardId(board.getId());

            var labels = cardLabels.get(card.getId());
            if (labels != null) {
                cardDto.setSelectedLabels(
                        labels.stream()
                        .map(l -> mapper.map(l, BoardCardSelectedLabel.class))
                        .toList()
                );
            }

            var members = cardMembers.get(card.getId());
            if (members != null) {
                cardDto.setMembers(
                        members.stream()
                        .map(m -> mapper.map(m, BoardCardMemberSimple.class))
                        .toList()
                );
            }

            return cardDto;
        };

        Function<BoardColumn, BoardColumnDto> toColumnDto = (column -> {
            var columnDto = mapper.map(column, BoardColumnDto.class);
            var boardCards = cards.get(column.getId());

            if (boardCards != null) {
                columnDto.setCards(boardCards.stream()
                                .filter(item -> {
                                    if (item.getIsDeleted() == null) {
                                        return true;
                                    }
                                    return !item.getIsDeleted();
                                })
                        .map(toCardDto)
                        .toList()
                );
            }
            return columnDto;
        });

        boardDto.setColumns(
                columns
                        .stream()
                        .filter(item -> {
                            if (item.getIsDeleted() == null) {
                                return true;
                            }
                            return !item.getIsDeleted();
                        })
                        .map(toColumnDto)
                        .toList()
        );

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
