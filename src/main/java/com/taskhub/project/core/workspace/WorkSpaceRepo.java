package com.taskhub.project.core.workspace;

import com.taskhub.project.core.workspace.domain.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkSpaceRepo extends JpaRepository<WorkSpace, String> {

    @Query(value = """
            SELECT ws.id AS id,
                   ws.title AS title,
                   COUNT(wm.user_id) AS userCount,
                   'MEMBER' AS 'type'
            FROM work_space ws
                     JOIN taskhup.workspace_member wm ON ws.id = wm.workspace_id
            WHERE ws.id IN (
                SELECT workspace_id
                FROM taskhup.workspace_member
                WHERE user_id = :userId
            )
            GROUP BY ws.id, ws.title
            UNION
            SELECT ws.id AS id,
                   ws.title AS title,
                   0 AS userCount,
                   'GUEST' AS 'type'
            FROM board_guest bg
                join board b on bg.board_id = b.id
                join work_space ws on b.workspace_id = ws.id
            WHERE bg.user_id = :userId
            """,
            nativeQuery = true
    )
    Optional<List<WorkSpace.UserWorkSpace>> getUserWorkSpaces(@Param("userId") String userId);
}
