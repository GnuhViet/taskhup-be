package com.taskhub.project.board.repo;

import com.taskhub.project.board.domain.BoardCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardCardRepo extends JpaRepository<BoardCard, String> {
}
