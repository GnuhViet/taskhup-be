package com.taskhub.project.core.board.service;

import com.taskhub.project.core.board.domain.BoardCardHistory;
import com.taskhub.project.core.board.repo.BoardCardHistoryRepo;
import com.taskhub.project.core.board.repo.CardCustomFieldRepo;
import com.taskhub.project.core.board.resources.api.model.boardCardDetails.BoardCardHistoryDetails;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
@Transactional
public class CardHistoryService {
    private final BoardCardHistoryRepo repo;
    private final CardCustomFieldRepo cardCustomFieldRepo;

    public static enum CardHistoryType {
        UPDATE_TITLE,
        SELECT_TEMPLATE,
        SELECT_LABEL,
        SELECT_FIELD,
        UPDATE_FIELD,
        UPDATE_MEMBER,
        UPDATE_CHECKLIST,
        UPDATE_CHECKLIST_VALUE,
        UPDATE_COVER,
        REMOVE_COVER,
        UPDATE_DATE,
        UPDATE_WORKING_STATUS,
        UPDATE_DESCRIPTION,
        UPLOAD_ATTACHMENT_CARD,
        UPLOAD_ATTACHMENT_COMMENT,
        DELETE_ATTACHMENT_CARD,
        DELETE_ATTACHMENT_COMMENT,
        CREATE_COMMENT,
        DELETE_COMMENT,
    }

    public CompletableFuture<Void> createHistoryAsync(
            String cardId,
            CardHistoryType type,
            String fromData,
            String toData,
            String userId
    ) {
        return CompletableFuture.runAsync(() -> createHistory(cardId, type, fromData, toData, userId));
    }


    public void createHistory(
            String cardId,
            CardHistoryType type,
            String fromData,
            String toData,
            String userId
    ) {

        if (type == CardHistoryType.UPDATE_FIELD) {
            var field = cardCustomFieldRepo.findById(toData).orElse(null);
            if (field == null) {
                return;
            }

            var cardHistory = BoardCardHistory.builder()
                    .boardCardId(cardId)
                    .type(type.name())
                    .fromData(fromData)
                    .toData(field.getTitle())
                    .createdAt(LocalDateTime.now())
                    .createdBy(userId)
                    .build();

            repo.save(cardHistory);

            return;
        }

        var cardHistory = BoardCardHistory.builder()
                .boardCardId(cardId)
                .type(type.name())
                .fromData(fromData)
                .toData(toData)
                .createdAt(LocalDateTime.now())
                .createdBy(userId)
                .build();

        repo.save(cardHistory);
    }

    public List<BoardCardHistoryDetails> getCardHistoryDetails(String cardId, boolean isLimit) {
        List<BoardCardHistory.Details> details = null;

        if (isLimit) {
            details = repo.findDetailsByCardId(cardId, PageRequest.of(0, 20));
        } else {
            details = repo.findDetailsByCardId(cardId);
        }

        List<BoardCardHistoryDetails> resp = new LinkedList<>();
        for (var detail : details) {
            resp.add(new BoardCardHistoryDetails(
                    detail.getId(),
                    detail.getType(),
                    detail.getToData(),
                    detail.getActionDate(),
                    detail.getUserId(),
                    detail.getUserName(),
                    detail.getUserFullName(),
                    detail.getUserAvatar()
            ));
        }

        return resp;
    }
}
