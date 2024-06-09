package com.taskhub.project.core.file;

import com.taskhub.project.core.file.domain.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FileInfoRepo extends JpaRepository<FileInfo, String> {

    @Query(value = """
    select * from file_info where id in :ids
    """, nativeQuery = true)
    List<FileInfo> findByListId(List<String> ids);
}
