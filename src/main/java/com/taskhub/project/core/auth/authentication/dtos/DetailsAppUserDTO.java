package com.taskhub.project.core.auth.authentication.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
