package com.taskhub.project.core.board.repo;

import com.taskhub.project.core.board.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepo extends JpaRepository<Board, String> {
    @Query(value = """
            select
                b.id as id,
                b.title as title,
                b.description as description,
                'JOINED' as type
            from board b
                join work_space ws on b.workspace_id = ws.id
            where ws.id in (
                select workspace_id
                from workspace_member
                where user_id = :userId
                and workspace_id = :workspaceId
            )
            union
            select
                b.id as id,
                b.title as title,
                b.description as description,
                'GUEST' as type
            from board b
                join work_space ws on b.workspace_id = ws.id
                join board_guest bg on b.id = bg.board_id
            where ws.id = :workspaceId
            and bg.user_id = :userId
            """, nativeQuery = true)
    Board.SimpleBoard getUserJoinedWorkSpaceBoard(@Param("userId") String userId, @Param("workspaceId") String workspaceId);


    @Query(value = """
            select
                b.id as id,
                b.title as title,
                b.description as description
            from board b
            where b.workspace_id = :workspaceId
       """, nativeQuery = true)
    List<Board.SimpleBoard> getBoardsByWorkSpaceId(@Param("workspaceId") String workspaceId);
}
