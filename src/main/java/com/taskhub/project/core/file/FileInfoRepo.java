package com.taskhub.project.core.file;

import com.taskhub.project.core.file.domain.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileInfoRepo extends JpaRepository<FileInfo, String> {
}
