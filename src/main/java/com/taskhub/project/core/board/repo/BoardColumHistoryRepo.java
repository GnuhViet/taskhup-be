package com.taskhub.project.core.board.repo;

import com.taskhub.project.core.board.domain.BoardColumnHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardColumHistoryRepo extends JpaRepository<BoardColumnHistory, String>{
}
