package com.taskhub.project.core.workspace;

import com.taskhub.project.core.workspace.domain.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkSpaceRepo extends JpaRepository<WorkSpace, String> {

    @Query(value = """
            (SELECT ws.id AS id,
                   ws.title AS title,
                   COUNT(wm.user_id) AS userCount,
                   'JOINED' AS type
            FROM work_space ws
                     JOIN taskhup.workspace_member wm ON ws.id = wm.workspace_id
            WHERE ws.id in (SELECT workspace_id
                            FROM taskhup.workspace_member wm1
                            WHERE user_id = :userId
                            AND wm1.invite_status = 'ACCEPTED')
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
            ORDER BY bg.join_date DESC)
            """,
            nativeQuery = true
    )
    Optional<List<WorkSpace.UserWorkSpace>> getUserWorkSpaces(@Param("userId") String userId);

    @Query("""
        select CASE WHEN COUNT(ws.id) > 0 THEN true ELSE false END
        from WorkSpace ws where ws.id = :workSpaceId and ws.ownerId = :userId
    """)
    boolean isWorkSpaceOwner(String workSpaceId, String userId);

    @Query("""
            select CASE WHEN COUNT(ws.id) > 0 THEN true ELSE false END
            from WorkSpace ws
                    join Board b on ws.id = b.workspace.id
            where b.id = :boardId and ws.ownerId = :userId
    """)
    boolean isWorkSpaceOwnerByBoardId(String boardId, String userId);

    @Query(value = """
        select ws.id as id
        from work_space ws
        join board b on ws.id = b.workspace_id
        where b.id = :boardId
    """, nativeQuery = true)
    WorkSpace.IdOnly getWorkSpaceIdByBoardId(String boardId);


    @Query(value = """
        select
            ws.id as id,
            ws.title as title,
            ws.description as description,
            (select u.username from app_user u where u.id = ws.owner_id) as ownerName,
            ws.website as website,
            fi.url as avatarUrl
        from work_space ws
            left join file_info fi on ws.avatar_id = fi.id
        where ws.id = :workSpaceId
    """, nativeQuery = true)
    WorkSpace.WorkSpaceInfo getWorkSpaceInfo(String workSpaceId);


    @Query(value = """
        select CASE WHEN COUNT(b.id) > 0 THEN 'true' ELSE 'false' END
        from work_space ws
            join board b on ws.id = b.workspace_id
        where ws.id = :workSpaceId and b.id = :boardId
    """, nativeQuery = true)
    boolean haveBoard(String workSpaceId, String boardId);
}