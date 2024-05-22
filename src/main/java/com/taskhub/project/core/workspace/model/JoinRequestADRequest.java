package com.taskhub.project.core.workspace.model;

import lombok.Data;

import java.util.List;

@Data
public class JoinRequestADRequest {
    List<String> userIds;
}
