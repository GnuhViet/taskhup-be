package com.taskhub.project.core.workspace.model;

import com.taskhub.project.core.workspace.dto.WorkSpaceDto;
import lombok.Data;

import java.util.List;

@Data
public class GetWorkSpaceResp {
    List<WorkSpaceDto> userWorkSpaces;
    List<WorkSpaceDto> joinedWorkSpaces;
    List<WorkSpaceDto> guestWorkSpaces;
}
