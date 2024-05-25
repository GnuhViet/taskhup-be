package com.taskhub.project.core.board.repo;

import com.taskhub.project.core.workspace.domain.WorkSpaceMember;
import com.taskhub.project.core.workspace.domain.WorkSpaceMemberKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkSpaceMemberRepo extends JpaRepository<WorkSpaceMember, WorkSpaceMemberKey> {
    // @Query("SELECT COUNT(ws) > 0 FROM WorkSpaceMember ws WHERE ws.workspace.id = :boardId AND ws.user.id = :userId")
    // @Query(value = "selecct * from workspace", nativeQuery = true)
    // boolean hasMember(String boardId, String userId);

    @Query("""
        SELECT CASE WHEN COUNT(wsm.id) > 0 THEN TRUE ELSE FALSE END
        FROM WorkSpaceMember wsm
        WHERE wsm.workspace.id = :workspaceId AND wsm.user.id = :userId
    """)
    boolean hasMember(String workspaceId, String userId);

    @Query("""
        SELECT CASE WHEN COUNT(bg.user.id) > 0 THEN TRUE ELSE FALSE END
        FROM BoardGuest bg
            join Board b on bg.id.boardId = b.id
            join WorkSpace ws on b.workspace.id = ws.id
        WHERE ws.id = :workspaceId AND bg.user.id = :userId
    """)
    boolean hasGuest(String workspaceId, String userId);

    Optional<WorkSpaceMember> findByWorkspaceIdAndUserId(String workspaceId, String userId);

    @Query(value = """
        select
            wm.user_id as `userId`,
            wm.role_id as `roleId`,
            wm.workspace_id as `workspaceId`,
            u.id as `userId`,
            u.username as `userName`,
            u.full_name as `fullName`,
            wm.join_date as `joinDate`,
            fi.url as `avatarUrl`,
            wm.invite_status as `status`
        from workspace_member wm
            join app_user u on wm.user_id = u.id
            join file_info fi on u.avatar = fi.id
        where wm.workspace_id = :workspaceId
        and (wm.invite_status = 'ACCEPTED' or wm.invite_status = 'DISABLED' or wm.invite_status IS NULL)
    """, nativeQuery = true)
    List<WorkSpaceMember.WorkspaceMemberDetails> getWorkspaceMember(String workspaceId);

    @Query(value = """
        select
            wm.role_id as `roleId`,
            wm.workspace_id as `workspaceId`,
            u.id as `userId`,
            u.username as `userName`,
            u.full_name as `fullName`,
            wm.join_date as `joinDate`
        from workspace_member wm
            join app_user u on wm.user_id = u.id
        where wm.workspace_id = :workspaceId
        and wm.invite_status = 'WAITING'
    """, nativeQuery = true)
    List<WorkSpaceMember.WorkspaceMemberDetails> getJoinRequestMember(String workspaceId);
}
