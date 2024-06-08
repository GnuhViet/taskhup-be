package com.taskhub.project.core.board.repo;

import com.taskhub.project.core.board.domain.BoardCardAttachments;
import com.taskhub.project.core.file.domain.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardCardAttachmentsRepo extends JpaRepository<BoardCardAttachments, String> {

    @Query(value = """
        select *
        from file_info fi
            join board_card_attachments bca on fi.id = bca.file_id
    """, nativeQuery = true)
    List<FileInfo> findByCardId(String cardId);
}
