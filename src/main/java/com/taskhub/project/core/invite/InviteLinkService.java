package com.taskhub.project.core.invite;

import com.taskhub.project.aspect.exception.model.ErrorsData;
import com.taskhub.project.comon.CommonFunction;
import com.taskhub.project.comon.service.model.ServiceResult;
import com.taskhub.project.core.board.domain.BoardGuest;
import com.taskhub.project.core.board.domain.BoardGuestKey;
import com.taskhub.project.core.board.domain.WorkSpaceMember;
import com.taskhub.project.core.board.domain.WorkSpaceMemberKey;
import com.taskhub.project.core.board.repo.BoardGuestRepo;
import com.taskhub.project.core.board.repo.BoardRepo;
import com.taskhub.project.core.board.repo.WorkSpaceMemberRepo;
import com.taskhub.project.core.helper.validator.ValidatorService;
import com.taskhub.project.core.invite.domain.InviteLink;
import com.taskhub.project.core.invite.model.AcceptInviteLinkResp;
import com.taskhub.project.core.invite.model.CreateInviteLinkReq;
import com.taskhub.project.core.user.constans.DefaultRole;
import com.taskhub.project.core.user.repo.RoleRepo;
import com.taskhub.project.core.user.repo.UserRepo;
import com.taskhub.project.core.workspace.WorkSpaceRepo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@AllArgsConstructor
@Transactional
public class InviteLinkService {
    private final ValidatorService validator;
    private final ModelMapper mapper;

    private final InviteLinkRepo inviteLinkRepo;
    private final BoardRepo boardRepo;
    private final WorkSpaceRepo workSpaceRepo;
    private final WorkSpaceMemberRepo workSpaceMemberRepo;
    private final BoardGuestRepo boardGuestRepo;
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;

    private static final long EXPIRE_DAYS = 1;

    @Getter
    public enum InviteLinkType {
        WORKSPACE("WORKSPACE"),
        BOARD("BOARD");

        private final String value;

        InviteLinkType(String value) {
            this.value = value;
        }
    }

    public ServiceResult<String> createInviteLink(String userId, CreateInviteLinkReq req) {
        validator.doValidate(req)
                .withConstraint(
                        () -> {
                            var type = InviteLinkType.valueOf(req.getType().toUpperCase());
                            boolean res;
                            switch (type) {
                                case WORKSPACE -> res = workSpaceRepo
                                        .findById(req.getDestinationId())
                                        .orElse(null) == null;
                                case BOARD -> res = boardRepo
                                        .findById(req.getDestinationId())
                                        .orElse(null) == null;
                                default -> res = false;
                            }
                            return res;
                        },
                        ErrorsData.of("destinationId", "not.found", "Destination not found")
                )
                .throwIfFails();

        var inviteLink = new InviteLink();
        inviteLink.setDestinationId(req.getDestinationId());
        inviteLink.setType(req.getType());
        inviteLink.setCreateDate(LocalDateTime.now());
        inviteLink.setExpireDate(LocalDateTime.now().plusDays(EXPIRE_DAYS));
        inviteLink.setCreateBy(userId);

        var linkId = CommonFunction.generateRandomString();
        while (true) { // BAD ? may bea
            if (inviteLinkRepo.existsById(linkId)) {
                linkId = CommonFunction.generateRandomString();
            } else {
                break;
            }
        }

        inviteLink.setId(linkId);

        inviteLinkRepo.save(inviteLink);

        return ServiceResult.created(
                String.valueOf(req.getType().charAt(0)).toLowerCase() + "_" + linkId
        );
    }


    public ServiceResult<AcceptInviteLinkResp> acceptInvite(String userId, String id) {
        final InviteLink[] inviteLink = new InviteLink[1];
        validator.tryValidate(id)
                .withConstraint(
                        () -> {
                            inviteLink[0] = inviteLinkRepo.findById(id.split("_")[1]).orElse(null);
                            return inviteLink[0] == null;
                        },
                        ErrorsData.of("id", "not found", "invite link not found")
                )
                .withConstraint(
                        () -> {
                            if (inviteLink[0] == null) return false;
                            return inviteLink[0].getExpireDate().isBefore(LocalDateTime.now());
                        },
                        ErrorsData.of("id", "expired", "invite link expired")
                )
                .throwIfFails();

        var link = inviteLink[0];

        switch (InviteLinkType.valueOf(link.getType().toUpperCase())) {
            case WORKSPACE -> {
                var ws = workSpaceRepo.findById(link.getDestinationId()).orElse(null);
                if (ws == null) return ServiceResult.notFound();
                if (workSpaceMemberRepo.hasMember(ws.getId(), userId)) break;

                var wsm = WorkSpaceMember.builder()
                        .id(new WorkSpaceMemberKey(userId, ws.getId()))
                        .joinDate(LocalDateTime.now())
                        .workspace(ws)
                        .build();

                workSpaceMemberRepo.save(wsm);
            }
            case BOARD -> {
                var board = boardRepo.findById(link.getDestinationId()).orElse(null);
                if (board == null) return ServiceResult.notFound();
                if (boardGuestRepo.hasGuest(board.getId(), userId)) break;

                var bg = BoardGuest.builder()
                        .id(new BoardGuestKey(userId, board.getId()))
                        .joinDate(LocalDateTime.now())
                        .build();

                boardGuestRepo.save(bg);
            }
        }


        return ServiceResult.ok(new AcceptInviteLinkResp(link.getType(), link.getDestinationId()));
    }
}
