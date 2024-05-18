package com.taskhub.project.core.workspace;

import com.taskhub.project.common.service.model.ServiceResult;
import com.taskhub.project.core.auth.authorization.constans.Action;
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
import com.taskhub.project.core.workspace.model.GetWorkSpaceResp;
import com.taskhub.project.core.workspace.model.WorkSpaceCreateReq;
import com.taskhub.project.core.workspace.model.WorkSpaceCreateResp;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class WorkSpaceService {
    private final ValidatorService validator;
    private final ModelMapper mapper;

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final WorkSpaceRepo workSpaceRepo;
    private final BoardRepo boardRepo;
    private final WorkSpaceMemberRepo workSpaceMemberRepo;

    public ServiceResult<?> getWorkSpaceMembers(String id) {
        return ServiceResult
                .ok(workSpaceMemberRepo.getWorkspaceMember(id)); // test api da viet
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
                .build();

        workSpaceMemberRepo.save(member);

        return ServiceResult.created(mapper.map(workSpace, WorkSpaceCreateResp.class));
    }

    public ServiceResult<GetWorkSpaceResp> getWorkSpace(String userId) {
        List<WorkSpace.UserWorkSpace> userWorkSpaceList = workSpaceRepo.getUserWorkSpaces(userId).orElse(null);

        if (userWorkSpaceList == null) {
            return ServiceResult.notFound();// TODO
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
            var boards = boardRepo.getBoardsByWorkSpaceId(item.getId());
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
}
