package com.taskhub.project.core.authentication.dtos;

import com.taskhub.project.core.user.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetailsAppUserDTO {
    private String id;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String avatar;
}
