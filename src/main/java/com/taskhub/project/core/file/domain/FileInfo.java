package com.taskhub.project.core.file.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "file_info")
public class FileInfo {
    @Id
    private String id;

    private String signature;

    private String format;

    private String resourceType;

    private String url;

    private String originFileName;

    private Long fileSize;
}
