package com.taskhub.project.core.board.service;

import com.taskhub.project.core.workspace.domain.BoardStar;
import com.taskhub.project.core.board.repo.BoardGuestRepo;
import com.taskhub.project.core.board.repo.BoardStarRepo;
import com.taskhub.project.core.board.repo.WorkSpaceMemberRepo;
import com.taskhub.project.core.helper.validator.ValidatorService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class BoardStarService {
    private final ValidatorService validator;
    private final BoardStarRepo boardStarRepo;
    private final WorkSpaceMemberRepo workSpaceMemberRepo;
    private final BoardGuestRepo boardGuestRepo;

    public ResponseEntity<BoardStar> starBoard(BoardStar req) {
        // validator
        //         .tryValidate(req)
        //         .withConstraint(
        //                 () ->  {
        //                     var isMember = workSpaceMemberRepo.hasMember(req.getBoardId(), req.getUserId());
        //                     var isGuest = boardGuestRepo.hasGuest(req.getBoardId(), req.getUserId());
        //                 },
        //                 ErrorsData.of("boardId", "INVALID", "Invalid action")
        //         )
        //         .throwIfFails();
        //
        throw new UnsupportedOperationException();
    }
}
