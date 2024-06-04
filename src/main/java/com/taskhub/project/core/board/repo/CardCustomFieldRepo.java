package com.taskhub.project.core.board.repo;

import com.taskhub.project.core.board.domain.CardCustomField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardCustomFieldRepo extends JpaRepository<CardCustomField, String> {
    List<CardCustomField> findByTemplateIdOrderByCreateDate(String templateId);
}
