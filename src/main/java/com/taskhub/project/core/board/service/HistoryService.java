package com.taskhub.project.core.board.service;

import com.taskhub.project.core.board.domain.BoardCardHistory;
import com.taskhub.project.core.board.domain.BoardColumnHistory;
import com.taskhub.project.core.board.repo.BoardCardCommentsRepo;
import com.taskhub.project.core.board.repo.BoardCardHistoryRepo;
import com.taskhub.project.core.board.repo.CardCustomFieldRepo;
import com.taskhub.project.core.board.repo.BoardColumHistoryRepo;
import com.taskhub.project.core.board.resources.api.model.DeleteRequest;
import com.taskhub.project.core.board.resources.api.model.ReviewRequest;
import com.taskhub.project.core.board.resources.api.model.boardCardDetails.BoardCardHistoryDetails;
import com.taskhub.project.core.user.model.NotificationResp;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
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
public class HistoryService {
    private final BoardCardHistoryRepo repo;
    private final CardCustomFieldRepo cardCustomFieldRepo;
    private final BoardCardCommentsRepo boardCardCommentsRepo;
    private final BoardColumHistoryRepo boardColumnHistoryRepo;
    private final ModelMapper mapper;

    public enum CardHistoryType {
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
        DELETE_CARD
    }

    public enum ColumnHistoryType {
        DELETE_COLUMN
    }

    public CompletableFuture<Void> createCardHistoryAsync(
            String cardId,
            CardHistoryType type,
            String fromData,
            String toData,
            String userId
    ) {
        return CompletableFuture.runAsync(() -> createCardHistory(cardId, type, fromData, toData, userId));
    }

    public CompletableFuture<Void> createColumnHistoryAsync(
            String columnId,
            ColumnHistoryType type,
            String fromData,
            String toData,
            String userId
    ) {
        return CompletableFuture.runAsync(() -> createColumnHistory(columnId, type, fromData, toData, userId));
    }


    public void createCardHistory(
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

        if (type == CardHistoryType.UPLOAD_ATTACHMENT_COMMENT || type == CardHistoryType.DELETE_ATTACHMENT_COMMENT) {
            var comment = boardCardCommentsRepo.findById(toData).orElse(null);
            if (comment == null) {
                return;
            }

            var cardHistory = BoardCardHistory.builder()
                    .boardCardId(comment.getBoardCardId())
                    .type(type.name())
                    .fromData(fromData)
                    .toData(toData)
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

    public void createColumnHistory(
            String columnId,
            ColumnHistoryType type,
            String fromData,
            String toData,
            String userId
    ) {
        var columnHistory = BoardColumnHistory.builder()
                .columnId(columnId)
                .type(type.name())
                .fromData(fromData)
                .toData(toData)
                .createdAt(LocalDateTime.now())
                .createdBy(userId)
                .build();

        boardColumnHistoryRepo.save(columnHistory);
    }

    public List<BoardCardHistoryDetails> getCardHistoryDetails(String cardId, boolean isLimit) {
        List<BoardCardHistory.Details> details = null;

        if (isLimit) {
            details = repo.findDetailsByCardId(cardId, PageRequest.of(0, 10));
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

    public List<NotificationResp> getUserNotification(String userId, boolean isUnreadOnly) {
        List<BoardCardHistory.Notification> notifications =
                repo.findNotificationByUserId(userId, isUnreadOnly);

        return notifications.stream()
                .map(item -> mapper.map(item, NotificationResp.class))
                .toList();
    }

    public List<ReviewRequest> getReview(String boardId) {
        List<BoardCardHistory.Review> reviews = repo.getReviewRequest(boardId);

        return reviews.stream()
                .map(item -> mapper.map(item, ReviewRequest.class))
                .toList();
    }

    public List<DeleteRequest> getDelete(String boardId) {
        List<BoardCardHistory.Delete> reviews = repo.getCardDeleteRequest(boardId);

        return reviews.stream()
                .map(item -> mapper.map(item, DeleteRequest.class))
                .toList();
    }
}
