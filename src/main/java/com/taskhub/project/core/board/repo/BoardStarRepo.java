package com.taskhub.project.core.board.repo;

import com.taskhub.project.core.board.domain.BoardStar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardStarRepo extends JpaRepository<BoardStar, String> {
}
