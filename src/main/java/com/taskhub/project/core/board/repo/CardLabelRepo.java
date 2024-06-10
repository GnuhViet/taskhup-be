package com.taskhub.project.core.board.repo;

import com.taskhub.project.core.board.domain.CardLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CardLabelRepo extends JpaRepository<CardLabel, String> {
    List<CardLabel> findByTemplateIdOrderByCreateDate(String templateId);

    @Query(value = """
        select * from card_label cl
        where cl.id in (:listId)
        order by cl.create_date
    """, nativeQuery = true)
    List<CardLabel> findByListId(List<String> listId);
}
