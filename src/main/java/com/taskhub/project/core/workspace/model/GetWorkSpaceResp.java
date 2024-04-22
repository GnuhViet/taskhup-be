package com.taskhub.project.core.workspace.model;

import com.taskhub.project.core.workspace.dto.SimpleWorkSpaceDto;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class GetWorkSpaceResp {
    List<SimpleWorkSpaceDto> joinedWorkSpaces = new LinkedList<>();
    List<SimpleWorkSpaceDto> guestWorkSpaces = new LinkedList<>();
}
