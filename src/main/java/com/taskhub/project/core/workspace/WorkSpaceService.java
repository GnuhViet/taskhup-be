package com.taskhub.project.core.workspace;

import com.taskhub.project.aspect.exception.model.ErrorsData;
import com.taskhub.project.common.service.model.ServiceResult;
import com.taskhub.project.core.auth.authorization.constans.Action;
import com.taskhub.project.core.file.FileService;
import com.taskhub.project.core.file.domain.FileInfo;
import com.taskhub.project.core.invite.InviteLinkService;
import com.taskhub.project.core.workspace.domain.WorkSpaceMember;
import com.taskhub.project.core.workspace.domain.WorkSpaceMemberKey;
import com.taskhub.project.core.board.repo.BoardRepo;
import com.taskhub.project.core.board.repo.WorkSpaceMemberRepo;
import com.taskhub.project.core.helper.validator.ValidatorService;
import com.taskhub.project.core.auth.authorization.constans.DefaultRole;
import com.taskhub.project.core.auth.authorization.RoleRepo;
import com.taskhub.project.core.user.repo.UserRepo;
import com.taskhub.project.core.workspace.domain.WorkSpace;
import com.taskhub.project.core.workspace.dto.SimpleBoardDto;
import com.taskhub.project.core.workspace.dto.SimpleWorkSpaceDto;
import com.taskhub.project.core.workspace.model.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class WorkSpaceService {
    private final ValidatorService validator;
    private final ModelMapper mapper;

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final WorkSpaceRepo workSpaceRepo;
    private final BoardRepo boardRepo;
    private final WorkSpaceMemberRepo workSpaceMemberRepo;
    private final FileService fileService;

    public WorkSpaceService(
            ValidatorService validator,
            ModelMapper mapper,
            UserRepo userRepo,
            RoleRepo roleRepo,
            WorkSpaceRepo workSpaceRepo,
            BoardRepo boardRepo,
            WorkSpaceMemberRepo workSpaceMemberRepo,
            @Qualifier("cloudinaryFileService") FileService fileService
    ) {
        this.validator = validator;
        this.mapper = mapper;
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.workSpaceRepo = workSpaceRepo;
        this.boardRepo = boardRepo;
        this.workSpaceMemberRepo = workSpaceMemberRepo;
        this.fileService = fileService;
    }

    public ServiceResult<?> getWorkSpaceMembers(String id) {
        return ServiceResult
                .ok(workSpaceMemberRepo.getWorkspaceMember(id)); // test api da viet
    }

    public ServiceResult<?> getWorkSpaceMemberWaiting(String id) {
        return ServiceResult
                .ok(workSpaceMemberRepo.getJoinRequestMember(id)); // test api da viet
    }

    public ServiceResult<?> getWorkSpaceInfo(String workspaceId) {
        return ServiceResult
                .ok(workSpaceRepo.getWorkSpaceInfo(workspaceId));
    }

    public ServiceResult<?> updateWorkSpaceAvatar(MultipartFile file, String workspaceId) {
        var workSpace = workSpaceRepo.findById(workspaceId).orElse(null);

        if (workSpace == null) {
            return ServiceResult.notFound();
        }

        var oldAvatar = workSpace.getAvatarId();
        if (oldAvatar != null) {
            var resp = fileService.deleteFile(oldAvatar);
            if (!fileService.isDeleteSuccess(resp)) {
                return ServiceResult.error("Internal server error: Failed to delete old avatar");
            }
        }

        var fileInfo = fileService.uploadFile(file);
        if (!fileService.isUploadSuccess(fileInfo)) {
            return ServiceResult.error("Internal server error: Failed to upload avatar");
        }

        workSpace.setAvatarId(((FileInfo) fileInfo.getData()).getId());

        workSpaceRepo.save(workSpace);
        return ServiceResult.ok("Avatar updated successfully");
    }

    public ServiceResult<?> updateWorkSpaceInfo(WorkSpaceUpdateInfoRequest request, String workspaceId) {
        final WorkSpace[] workSpaceDb = new WorkSpace[1];

        validator.tryValidate(request)
                .withConstraint(
                        () -> {
                            workSpaceDb[0] = workSpaceRepo.findById(workspaceId).orElse(null);
                            return workSpaceDb[0] == null;
                        },
                        ErrorsData.of("Workspace not found", "04", workspaceId)
                )
                .throwIfFails();

        var workSpace = workSpaceDb[0];
        workSpace.setTitle(request.getTitle());
        workSpace.setDescription(request.getDescription());
        workSpace.setWebsite(request.getWebsite());

        workSpaceRepo.save(workSpace);
        return ServiceResult.ok("Workspace updated successfully");
    }

    public ServiceResult<?> disableWorkspaceMember(DisabledMemberRequest req, String workspaceId) {
        var memberDb = new WorkSpaceMember[2];
        validator.tryValidate(req)
                .withConstraint(
                        () -> {
                            memberDb[0] = workSpaceMemberRepo.findByWorkspaceIdAndUserId(workspaceId, req.getMemberId()).orElse(null);
                            return memberDb[0] == null;
                        },
                        ErrorsData.of("Member not found", "04", req.getMemberId())
                )
                .throwIfFails();

        var member = memberDb[0];
        member.setInviteStatus(InviteLinkService.MemberStatus.DISABLED.value);
        workSpaceMemberRepo.save(member);

        return ServiceResult.ok(null);
    }

    public ServiceResult<?> activeWorkspaceMember(DisabledMemberRequest req, String workspaceId) {
        var memberDb = new WorkSpaceMember[2];
        validator.tryValidate(req)
                .withConstraint(
                        () -> {
                            memberDb[0] = workSpaceMemberRepo.findByWorkspaceIdAndUserId(workspaceId, req.getMemberId()).orElse(null);
                            return memberDb[0] == null;
                        },
                        ErrorsData.of("Member not found", "04", req.getMemberId())
                )
                .throwIfFails();

        var member = memberDb[0];
        member.setInviteStatus(InviteLinkService.MemberStatus.ACCEPTED.value);
        workSpaceMemberRepo.save(member);

        return ServiceResult.ok(null);
    }

    @Getter
    @AllArgsConstructor
    public enum WorkSpaceMemberType {
        JOINED("JOINED"),
        GUEST("GUEST");
        public final String value;
    }

    @Valid
    public ServiceResult<WorkSpaceCreateResp> createWorkSpace(WorkSpaceCreateReq request, String userId) {
        validator.doValidate(request);

        var workSpace =  workSpaceRepo.save(mapper.map(request, WorkSpace.class));

        workSpace.setCreateBy(userId);
        workSpace.setOwnerId(userId);
        workSpace.setCreateDate(LocalDateTime.now());

        var user = userRepo.getReferenceById(userId);
        var role = roleRepo.getReferenceById(DefaultRole.OWNER.getId());

        WorkSpaceMember member = WorkSpaceMember.builder()
                .id(new WorkSpaceMemberKey(userId, workSpace.getId()))
                .user(user)
                .role(role)
                .workspace(workSpace)
                .joinDate(LocalDateTime.now())
                .inviteStatus(InviteLinkService.MemberStatus.ACCEPTED.value)
                .build();

        workSpaceMemberRepo.save(member);

        return ServiceResult.created(mapper.map(workSpace, WorkSpaceCreateResp.class));
    }

    public ServiceResult<GetWorkSpaceResp> getWorkSpace(String userId) {
        List<WorkSpace.UserWorkSpace> userWorkSpaceList = workSpaceRepo.getUserWorkSpaces(userId).orElse(null);

        if (userWorkSpaceList == null) {
            return ServiceResult.notFound();// TODâ
        }

        GetWorkSpaceResp resp = new GetWorkSpaceResp();

        userWorkSpaceList.forEach(item -> {
            if (WorkSpaceMemberType.JOINED.value.equals(item.getType())) {
                var workspace = mapper.map(item, SimpleWorkSpaceDto.class);
                var actionCode = roleRepo.getActionList(item.getId(), userId).getActionCode();
                workspace.setCanCreateBoard(
                        actionCode != null && actionCode.contains(Action.EDIT_BOARD.getCode())
                );
                resp.getJoinedWorkSpaces().add(workspace);
            }
            else
                if (WorkSpaceMemberType.GUEST.value.equals(item.getType())) {
                var workspace = mapper.map(item, SimpleWorkSpaceDto.class);
                workspace.setCanCreateBoard(false);
                resp.getGuestWorkSpaces().add(workspace);
            }
        });

        resp.getJoinedWorkSpaces().forEach(item -> {
            var boards = boardRepo.getBoardsByWorkSpaceId(item.getId(), userId);
            item.setBoards(boards.stream().map(board -> mapper.map(board, SimpleBoardDto.class)).toList());
        });

        resp.getGuestWorkSpaces().forEach(item -> {
            var boards = boardRepo.getGuestBoards(item.getId(), userId);
            item.setBoards(boards.stream().map(board -> mapper.map(board, SimpleBoardDto.class)).toList());
        });

        return ServiceResult.ok(resp);
    }

    public ServiceResult<SimpleBoardDto> getUserWorkSpaceBoards(String userId, String workspaceId) {
        return null;
    }

    public ServiceResult<?> acceptWorkspaceMember(List<String> userIds, String workspaceIds) {
        for (String userId : userIds) {
            var member = workSpaceMemberRepo.findByWorkspaceIdAndUserId(workspaceIds, userId);
            if (member.isEmpty()) {
                return ServiceResult.notFound();
            }
            member.get().setInviteStatus((InviteLinkService.MemberStatus.ACCEPTED.value));
            workSpaceMemberRepo.save(member.get());
        }
        return ServiceResult.ok(userIds);
    }

    public ServiceResult<?> denyWorkspaceMember(List<String> userIds, String workspaceIds) {
        for (String userId : userIds) {
            var member = workSpaceMemberRepo.findByWorkspaceIdAndUserId(workspaceIds, userId);
            if (member.isEmpty()) {
                return ServiceResult.notFound();
            }
            workSpaceMemberRepo.delete(member.get());
        }
        return ServiceResult.ok(userIds);
    }
}
