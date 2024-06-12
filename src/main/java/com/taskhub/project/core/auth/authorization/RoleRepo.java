package com.taskhub.project.core.auth.authorization;

import com.taskhub.project.core.auth.authorization.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoleRepo extends JpaRepository<Role, String> {


    @Query("""
        select r
        from Role r
        join WorkSpaceMember wsm on r.id = wsm.role.id
        where wsm.user.id = :userId
        and wsm.workspace.id = :workspaceId
    """)
    Role findWorkspaceMemberRole(String userId, String workspaceId);

    @Query("""
        select r
        from Role r
            join BoardGuest bg on r.id = bg.role.id
        where bg.user.id = :userId
    """)
    Role findWorkspaceGuestRole(String userId);

    @Query("""
        select r.actionCode as actionCode
        from Role r
            join WorkSpaceMember wsm on r.id = wsm.role.id
        where wsm.user.id = :userId
            and wsm.workspace.id = :workspaceId
    """)
    Role.ActionOnly getActionList(String workspaceId, String userId);

    @Query(value = """
        SELECT r.id                                     AS id,
               r.name                                   AS name,
               r.color                                  AS color,
               u.full_name                              AS createBy,
               r.create_date                            AS createDate,
               r.action_code                            AS actionCode,
               GROUP_CONCAT(DISTINCT wsm.user_id)       AS memberIds
        FROM role r
                 LEFT JOIN workspace_member wsm ON r.id = wsm.role_id
                 LEFT JOIN app_user u ON r.create_by = u.id
        WHERE r.id IN ('default-role-member', 'default-role-owner', 'default-role-guest')
           OR (r.workspace_id = :workspaceId AND
               r.id NOT IN ('default-role-member', 'default-role-owner', 'default-role-guest'))
        GROUP BY r.id, r.create_date
        ORDER BY
            CASE
                WHEN r.id = 'default-role-owner'  THEN 1
                WHEN r.id = 'default-role-member' THEN 2
                WHEN r.id = 'default-role-guest'  THEN 3
                ELSE 4
                END,
            CASE
                WHEN r.id NOT IN ('default-role-owner', 'default-role-member', 'default-role-guest') THEN r.create_date
                END DESC;
    """, nativeQuery = true)
    List<Role.RoleWithMember> findByWorkSpaceId(String workspaceId);



    @Query(value = """
        SELECT GROUP_CONCAT(DISTINCT wsm.user_id)       AS memberIds
        FROM role r
                 JOIN workspace_member wsm ON r.id = wsm.role_id
                 JOIN app_user u ON r.create_by = u.id
        WHERE r.id = :roleId
        and wsm.workspace_id = :workspaceId
    """, nativeQuery = true)
    Role.RoleMemberList getMemberList(String roleId, String workspaceId);
}
