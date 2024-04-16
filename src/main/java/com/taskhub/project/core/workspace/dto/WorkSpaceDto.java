package com.taskhub.project.core.workspace.dto;

import com.taskhub.project.core.board.domain.Board;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class WorkSpaceDto {
    private String id;
    private String title;
    private String description;
    private List<SimpleBoardDto> boards;
}
