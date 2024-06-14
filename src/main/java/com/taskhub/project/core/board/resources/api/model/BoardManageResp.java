package com.taskhub.project.core.board.resources.api.model;

import lombok.Data;

import java.util.List;

@Data
public class BoardManageResp {
    private Boolean isOnlyMemberEdit;
    private Boolean isNeedReview;
    private List<ReviewRequest> reviewRequest;
    private List<DeleteRequest> deleteRequest;
}
