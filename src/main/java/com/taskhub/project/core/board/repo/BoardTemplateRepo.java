package com.taskhub.project.core.board.repo;

import com.taskhub.project.core.board.domain.BoardCardTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardTemplateRepo extends JpaRepository<BoardCardTemplate, String> {

    @Query(value = """
        select
            bct.id,
            bct.title,
            fi.url as avatarUrl,
            (select count(*) from board_card bc where bc.template_id = bct.id) as `usedIn`
        from
            board_card_template bct
            left join file_info fi on bct.avatar = fi.id
        where
            bct.board_id = :boardId
        order by bct.create_date
    """, nativeQuery = true)
    List<BoardCardTemplate.BoardTemplateDetail> getTemplate(String boardId);

    @Query(value = """
        select CASE WHEN COUNT(bc.id) > 0 THEN 'true' ELSE 'false' END
        from board_card bc where bc.template_id = :templateId
    """, nativeQuery = true)
    boolean isUsed(String templateId);

    @Query(value = """
        select CASE WHEN COUNT(bct.id) > 0 THEN 'true' ELSE 'false' END
        from work_space ws
            join board b on ws.id = b.workspace_id
            join board_card_template bct on b.id = bct.board_id
        where bct.id = :templateId and ws.id = :workspaceId
    """, nativeQuery = true)
    boolean isBelongToWorkSpace(String templateId, String workspaceId);

    @Query(value = """
        select CASE WHEN COUNT(cl.id) > 0 THEN 'false' ELSE 'true' END
        from card_label cl where cl.id IN :labelIds and cl.template_id != :templateId
    """, nativeQuery = true)
    boolean hasLabel(String templateId, List<String> labelIds);

    @Query(value = """
        select CASE WHEN COUNT(cf.id) > 0 THEN 'false' ELSE 'true' END
        from card_custom_field cf where cf.id IN :fieldIds and cf.template_id != :templateId
    """, nativeQuery = true)
    boolean hasField(String templateId, List<String> fieldIds);
}
