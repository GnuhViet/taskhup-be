package com.taskhub.project.core.board.repo;

import com.taskhub.project.core.board.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepo extends JpaRepository<Board, String> {
}