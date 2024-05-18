package com.taskhub.project.core.invite;

import com.taskhub.project.aspect.exception.model.ErrorsData;
import com.taskhub.project.common.CommonFunction;
import com.taskhub.project.common.service.model.ServiceResult;
import com.taskhub.project.core.auth.authorization.constans.DefaultRole;
import com.taskhub.project.core.workspace.domain.BoardGuest;
import com.taskhub.project.core.workspace.domain.BoardGuestKey;
import com.taskhub.project.core.workspace.domain.WorkSpaceMember;
import com.taskhub.project.core.workspace.domain.WorkSpaceMemberKey;
import com.taskhub.project.core.board.repo.BoardGuestRepo;
import com.taskhub.project.core.board.repo.BoardRepo;
import com.taskhub.project.core.board.repo.WorkSpaceMemberRepo;
import com.taskhub.project.core.helper.validator.ValidatorService;
import com.taskhub.project.core.invite.domain.InviteLink;
import com.taskhub.project.core.invite.model.InviteLinkCreateReq;
import com.taskhub.project.core.auth.authorization.RoleRepo;
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

        public final String value;

        InviteLinkType(String value) {
            this.value = value;
        }
    }

    @Getter
    public enum MemberStatus {
        WAITING("WAITING"),
        ACCEPTED("ACCEPTED");

        public final String value;

        MemberStatus(String value) {
            this.value = value;
        }
    }

    public ServiceResult<String> createInviteLink(String userId, InviteLinkCreateReq req) {
        validator.doValidate(req) // TODO validate ROLE
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
                // .withConstraint(
                //         () -> inviteLinkRepo.existsByDestinationId(req.getDestinationId()),
                //         ErrorsData.of("destinationId", "already.haveLink", "Destination already invite link")
                // )
                .withConstraint(
                        () -> {
                            var type = InviteLinkType.valueOf(req.getType().toUpperCase());
                            boolean res;
                            switch (type) {
                                case WORKSPACE -> res = workSpaceRepo.isWorkSpaceOwner(req.getDestinationId(), userId);
                                case BOARD -> res = workSpaceRepo.isWorkSpaceOwnerByBoardId(req.getDestinationId(), userId);
                                default -> res = true;
                            }
                            return !res;
                        },
                        ErrorsData.of("userId", "not.owner", "User not owner")
                )
                .throwIfFails();

        if (inviteLinkRepo.existsByDestinationId(req.getDestinationId())) {
            var link = inviteLinkRepo.findByDestinationId(req.getDestinationId());
            return ServiceResult.ok(
                    String.valueOf(link.getType().charAt(0)).toLowerCase() + "_" + link.getId()
            );
        }

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

    public ServiceResult<InviteLinkCreateReq> createJoinRequest(String userId, String id) {
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
                .withConstraint(
                        () -> {
                            var type = InviteLinkType.valueOf(inviteLink[0].getType().toUpperCase());
                            boolean res;
                            switch (type) {
                                case WORKSPACE -> res = workSpaceMemberRepo.hasMember(inviteLink[0].getDestinationId(), userId);
                                case BOARD -> res = boardGuestRepo.hasGuest(inviteLink[0].getDestinationId(), userId);
                                default -> res = false;
                            }
                            return res;
                        },
                        ErrorsData.of("userId", "already.in", "User already in")

                )
                .throwIfFails();

        var link = inviteLink[0];

        switch (InviteLinkType.valueOf(link.getType().toUpperCase())) {
            case WORKSPACE -> {
                var ws = workSpaceRepo.findById(link.getDestinationId()).orElse(null);
                if (ws == null) return ServiceResult.notFound();
                if (workSpaceMemberRepo.hasMember(ws.getId(), userId)) break;
                var role = roleRepo.getReferenceById(DefaultRole.MEMBER.getId());

                var wsm = WorkSpaceMember.builder()
                        .id(new WorkSpaceMemberKey(userId, ws.getId()))
                        .joinDate(LocalDateTime.now())
                        .inviteStatus(MemberStatus.WAITING.value)
                        .workspace(ws)
                        .user(userRepo.getReferenceById(userId))
                        .role(role)
                        .build();

                workSpaceMemberRepo.save(wsm);
            }
            case BOARD -> {
                var board = boardRepo.findById(link.getDestinationId()).orElse(null);
                if (board == null) return ServiceResult.notFound();
                if (boardGuestRepo.hasGuest(board.getId(), userId)) break;
                var role = roleRepo.getReferenceById(DefaultRole.GUEST.getId());

                var bg = BoardGuest.builder()
                        .id(new BoardGuestKey(userId, board.getId()))
                        .joinDate(LocalDateTime.now())
                        .inviteStatus(MemberStatus.WAITING.value)
                        .user(userRepo.getReferenceById(userId))
                        .board(board)
                        .role(role)
                        .build();

                boardGuestRepo.save(bg);
            }
        }


        return ServiceResult.ok(new InviteLinkCreateReq(link.getType(), link.getDestinationId()));
    }

    //TODO author
    public ServiceResult<String> getInviteLink(String userId, String destinationId) {
        // validator.validate()
        //         .withConstraint(
        //                 () -> {
        //
        //                 },
        //                 ErrorsData.of("destinationId", "not.found", "Destination not found")
        //         )
        //         .throwIfFails();

        var resp = inviteLinkRepo.findByDestinationId(destinationId);


        return ServiceResult.ok(String.valueOf(resp.getType().charAt(0)).toLowerCase() + "_" + resp.getId());
    }
}
