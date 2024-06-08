package com.taskhub.project.core.board.service;

import com.taskhub.project.core.board.domain.BoardCardHistory;
import com.taskhub.project.core.board.repo.BoardCardHistoryRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Transactional
public class CardHistoryService {
    private final BoardCardHistoryRepo repo;

    public static enum CardHistoryType {
        SELECT_TEMPLATE,
    }


    public void createHistory(
            String cardId,
            CardHistoryType type,
            String fromData,
            String toData
    ) {

        var cardHistory = BoardCardHistory.builder()
                .boardCardId(cardId)
                .type(type.name())
                .fromData(fromData)
                .toData(toData)
                .createdAt(LocalDateTime.now())
                .build();


        repo.save(cardHistory);
    }
}
