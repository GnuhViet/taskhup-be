package com.taskhub.project.core.auth.authorization;

import com.taskhub.project.aspect.exception.model.ErrorsData;
import com.taskhub.project.common.service.model.ServiceResult;
import com.taskhub.project.core.auth.authorization.constans.Action;
import com.taskhub.project.core.auth.authorization.constans.DefaultRole;
import com.taskhub.project.core.auth.authorization.domain.Role;
import com.taskhub.project.core.auth.authorization.model.*;
import com.taskhub.project.core.board.repo.BoardGuestRepo;
import com.taskhub.project.core.board.repo.WorkSpaceMemberRepo;
import com.taskhub.project.core.helper.validator.ValidatorService;
import com.taskhub.project.core.user.entities.AppUser;
import com.taskhub.project.core.user.repo.UserRepo;
import com.taskhub.project.core.workspace.WorkSpaceRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Service
@Transactional
public class RoleService {
    private final RoleRepo roleRepo;
    private final WorkSpaceRepo workSpaceRepo;
    private final UserRepo userRepo;
    private final WorkSpaceMemberRepo workSpaceMemberRepo;
    private final BoardGuestRepo boardGuestRepo;

    private final ValidatorService validator;
    private final ModelMapper mapper;
    private final DateTimeFormatter dateTimeFormatter;

    public ServiceResult<RoleGetResp> createRole(RoleCreateReq req, String userId, String workspaceId) {
        validator.tryValidate(req)
                .withConstraint(
                        () -> Action.validateAction(req.getActionCode()),
                        ErrorsData.of("actions", "RoleService.CreateRole", "Invalid action")
                )
                .throwIfFails();


        var role = new Role();
        role.setName(req.getName());
        role.setColor(req.getColor());
        role.setActionCode(String.join(",", req.getActionCode()));
        role.setCreateBy(userId);
        role.setWorkspaceId(workspaceId);
        role.setCreateDate(LocalDateTime.now());

        roleRepo.save(role);

        var fullName = userRepo.getFullNameById(userId).getFullName();

        return ServiceResult.ok(
                RoleGetResp.builder()
                        .id(role.getId())
                        .name(role.getName())
                        .color(role.getColor())
                        .createBy(fullName)
                        .createDate(dateTimeFormatter.format(role.getCreateDate()))
                        .actionCode(req.getActionCode())
                        .member(null)
                        .build()
        );
    }

    public ServiceResult<List<RoleGetResp>> getRole(String workspaceId) {
        validator.validate()
                .withConstraint(
                        () -> !workSpaceRepo.existsById(workspaceId),
                        ErrorsData.of("workspaceId", "RoleService.GetRole", "Workspace not found")
                )
                .throwIfFails();

        return ServiceResult.ok(
                roleRepo.findByWorkSpaceId(workspaceId)
                    .stream()
                    .map(role -> mapper.map(role, RoleGetResp.class))
                    .toList()
        );

    }

    public ServiceResult<RoleUpdateReq> updateRole(RoleUpdateReq req, String userId) {
        final var dbRole = new Role[1];
        validator.tryValidate(req)
                .withConstraint( // sua them ham continue if fails?
                        () -> {
                            dbRole[0] = roleRepo.findById(req.getId()).orElse(null);
                            return dbRole[0] == null;
                        },
                        ErrorsData.of("id", "RoleService.UpdateActions.NotFound", "Role not found")
                )
                .withConstraint(
                        () -> DefaultRole.isDefaultRole(req.getId()),
                        ErrorsData.of("actions", "RoleService.UpdateActions.defaultRole", "Invalid action")
                )
                .withConstraint(
                        () -> !Objects.isNull(req.getActionCode()) && Action.validateAction(req.getActionCode()),
                        ErrorsData.of("actions", "RoleService.UpdateActions.ActionCode", "Invalid action")
                )
                .throwIfFails();
        var role = dbRole[0];

        var newActions = Objects.isNull(req.getActionCode()) ? role.getActionCode() : String.join(",", req.getActionCode());
        var newName = StringUtils.isBlank(req.getName()) ? role.getName() : req.getName();

        role.setActionCode(newActions);
        role.setName(newName);
        role.setUpdateBy(userId);
        role.setUpdateDate(LocalDateTime.now());

        roleRepo.save(role);

        return ServiceResult.ok(req);
    }
    // sai
    public ServiceResult<String> addMember(RoleAddMemberReq req, String workspaceId) {
        final var dbRole = new Role[1];
        final var dbUser = new AppUser[1];
        validator.tryValidate(req)
                .withConstraint(
                        () -> {
                            dbRole[0] = roleRepo.findById(req.getId()).orElse(null);
                            return dbRole[0] == null;
                        },
                        ErrorsData.of("id", "RoleService.UpdateActions", "Role not found")
                )
                .withConstraint(
                        () -> {
                            var isMember = workSpaceMemberRepo.hasMember(workspaceId, req.getUserId());
                            return !isMember;
                        },
                        ErrorsData.of("workspaceId", "workspace.invalid", "Invalid workspace")
                )
                .throwIfFails();
        var role = dbRole[0];

        var wsm = workSpaceMemberRepo.findByWorkspaceIdAndUserId(workspaceId, req.getUserId()).get();
        wsm.setRole(role);


        return ServiceResult.ok(roleRepo.getMemberList(role.getId(), workspaceId).getMember());
    }

    public ServiceResult<?> changeMemberRole(ChangeMemberRoleRequest req, String workspaceId) {
        final var dbRole = new Role[1];
        validator.tryValidate(req)
                .withConstraint(
                        () -> {
                            if (DefaultRole.isDefaultRole(req.getRoleId())) {
                                return false;
                            }
                            return !workSpaceRepo.haveRole(workspaceId, req.getRoleId());
                        },
                        ErrorsData.of("id", "04", "Role not belong to ws")
                )
                .withConstraint(
                        () -> {
                            dbRole[0] = roleRepo.findById(req.getRoleId()).orElse(null);
                            return dbRole[0] == null;
                        },
                        ErrorsData.of("id", "04", "Role not found")
                )
                .throwIfFails();

        var role = dbRole[0];

        var wsm = workSpaceMemberRepo.findByWorkspaceIdAndUserId(workspaceId, req.getMemberId()).get();
        wsm.setRole(role);

        return ServiceResult.ok(null);
    }
}
