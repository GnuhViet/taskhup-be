package com.taskhub.project.core.invite;

import com.taskhub.project.comon.CommonFunction;
import com.taskhub.project.comon.service.model.ServiceResult;
import com.taskhub.project.core.board.repo.BoardRepo;
import com.taskhub.project.core.invite.domain.InviteLink;
import com.taskhub.project.core.invite.model.CreateInviteLinkReq;
import com.taskhub.project.core.workspace.WorkSpaceRepo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.taskhub.project.core.invite.InviteLinkService.InviteLinkType.BOARD;
import static com.taskhub.project.core.invite.InviteLinkService.InviteLinkType.WORKSPACE;

@Service
@AllArgsConstructor
@Transactional
public class InviteLinkService {
    private final InviteLinkRepo inviteLinkRepo;
    private final BoardRepo boardRepo;
    private final WorkSpaceRepo workSpaceRepo;

    private static final long EXPIRE_DAYS = 1;


    @Getter
    public static enum InviteLinkType {
        WORKSPACE("WORKSPACE"),
        BOARD("BOARD");

        private final String value;

        InviteLinkType(String value) {
            this.value = value;
        }
    }

    public ServiceResult<String> createInviteLink(String userId, CreateInviteLinkReq req) {
        String id;
        var type = InviteLinkType.valueOf(req.getType());
        switch (type) {
            case WORKSPACE -> {
                var ws = workSpaceRepo.findById(req.getDestinationId()).orElse(null);
                if (ws == null) {
                    return ServiceResult.notFound();
                }
                id = ws.getId();
            }
            case BOARD -> {
                var b = boardRepo.findById(req.getDestinationId()).orElse(null);
                if (b == null) {
                    return ServiceResult.notFound();
                }
                id = b.getId();
            }
            default -> {
                return ServiceResult.badRequest();
            }
        }

        if (id == null) {
            return ServiceResult.badRequest();
        }

        var inviteLink = new InviteLink();
        inviteLink.setDestinationId(id);
        inviteLink.setType(type.getValue());
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

        return ServiceResult.created(type.getValue() + "_" + linkId);
    }


    public InviteLink getInviteLink(String id) {
        return inviteLinkRepo.findById(id).orElse(null);
    }
}
