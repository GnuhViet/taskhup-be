package com.taskhub.project.core.board.repo;

import com.taskhub.project.core.board.domain.WorkSpaceMember;
import com.taskhub.project.core.board.domain.WorkSpaceMemberKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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

    Optional<WorkSpaceMember> findByWorkspaceIdAndUserId(String workspaceId, String userId);
}
