package com.taskhub.project.core.workspace;

import com.taskhub.project.core.workspace.domain.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkSpaceRepo extends JpaRepository<WorkSpace, String> {

    @Query(value = """
            (
            SELECT ws.id AS id,
                   ws.title AS title,
                   COUNT(wm.user_id) AS userCount,
                   'JOINED' AS type
            FROM work_space ws
                     JOIN taskhup.workspace_member wm ON ws.id = wm.workspace_id
            WHERE ws.id in (SELECT workspace_id
                            FROM taskhup.workspace_member
                            WHERE user_id = :userId )
            GROUP BY ws.id, ws.title, type, ws.create_date
            ORDER BY ws.create_date DESC
            ) UNION ALL (
            SELECT ws.id AS id,
                   ws.title AS title,
                   0 AS userCount,
                   'GUEST' AS 'type'
            FROM board_guest bg
                     join board b on bg.board_id = b.id
                     join work_space ws on b.workspace_id = ws.id
            WHERE bg.user_id = :userId
            ORDER BY bg.join_date DESC
            )
            """,
            nativeQuery = true
    )
    Optional<List<WorkSpace.UserWorkSpace>> getUserWorkSpaces(@Param("userId") String userId);

    @Query("select CASE WHEN COUNT(ws.id) > 0 THEN true ELSE false END from WorkSpace ws where ws.id = :workSpaceId and ws.ownerId = :userId")
    boolean isWorkSpaceOwner(String workSpaceId, String userId);

    @Query(value = """
        select ws.id as id
        from work_space ws
        join board b on ws.id = b.workspace_id
        where b.id = :boardId
    """, nativeQuery = true)
    WorkSpace.IdOnly getWorkSpaceIdByBoardId(String boardId);
}
