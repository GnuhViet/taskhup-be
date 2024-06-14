package com.taskhub.project.core.board.repo;

import com.taskhub.project.core.board.domain.BoardHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardHistoryRepo extends JpaRepository<BoardHistory, String> {

}
