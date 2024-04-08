package com.taskhub.project.board.repo;

import com.taskhub.project.board.domain.BoardColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardColumnRepo extends JpaRepository<BoardColumn, String> {
}
