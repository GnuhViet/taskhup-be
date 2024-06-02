package com.taskhub.project.core.workspace.dto;

import lombok.Data;

import java.util.List;

@Data
public class SimpleWorkSpaceDto {
    private String id;
    private String title;
    private String type;
    private Boolean canCreateBoard;
    private String avatarUrl;
    private Long memberCount;
    private List<SimpleBoardDto> boards;
}
