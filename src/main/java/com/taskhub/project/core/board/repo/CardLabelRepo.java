package com.taskhub.project.core.board.repo;

import com.taskhub.project.core.board.domain.CardLabel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardLabelRepo extends JpaRepository<CardLabel, String> {
    List<CardLabel> findByTemplateIdOrderByCreateDate(String templateId);
}
