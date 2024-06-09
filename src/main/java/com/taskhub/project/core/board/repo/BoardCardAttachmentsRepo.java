package com.taskhub.project.core.board.repo;

import com.taskhub.project.core.board.domain.BoardCardAttachments;
import com.taskhub.project.core.file.domain.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardCardAttachmentsRepo extends JpaRepository<BoardCardAttachments, String> {

    @Query(value = """
        (
        select
            bca.id as id,
            bca.type as type,
            bca.ref_id as refId,
            bca.file_id as fileId,
            bca.upload_by as uploadBy,
            bca.upload_at as uploadAt,
            bca.display_name as displayName,
   
            fi.file_size as fileSize,
            fi.origin_file_name as originFileName,
            fi.format as format,
            fi.url as downloadUrl,
            fi.resource_type as resourceType
        from board_card_attachments bca
            left join file_info fi on bca.file_id = fi.id
        where
            bca.ref_id = :cardId
            and bca.type = 'CARD_ATTACH'
        order by bca.upload_at
        )
        union all
        (
        select
            bca.id as id,
            bca.type as type,
            bca.ref_id as refId,
            bca.file_id as fileId,
            bca.upload_by as uploadBy,
            bca.upload_at as uploadAt,
            bca.display_name as displayName,
   
            fi.file_size as fileSize,
            fi.origin_file_name as originFileName,
            fi.format as format,
            fi.url as downloadUrl,
            fi.resource_type as resourceType
        from board_card_attachments bca
            left join file_info fi on bca.file_id = fi.id
        where bca.ref_id in (
            select id from board_card_comments bcc1 where bcc1.board_card_id = :cardId
        )
        and bca.type = 'COMMENT_ATTACH'
        order by bca.upload_at desc
        )
    """, nativeQuery = true)
    List<BoardCardAttachments.BoardCardAttachmentsInfo> findByCardId(String cardId);

    @Query(value = """
        select *
        from board_card_attachments bca
        where bca.ref_id = :commentId
        and bca.type = 'COMMENT_ATTACH'
    """, nativeQuery = true)
    List<BoardCardAttachments> getByCommentId(String commentId);
}
