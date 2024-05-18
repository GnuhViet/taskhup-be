package com.taskhub.project.core.auth.authorization.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleGetResp {
    private String id;
    private String name;
    private String color;
    private String createBy;
    private String createDate;

    private List<String> actionCode;
    private List<String> member;
}
