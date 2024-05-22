package com.taskhub.project.core.auth.authentication.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateInfoRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;
    private String email;
    @NotBlank(message = "phoneNumber is required")
    private String phoneNumber;
    private String bio;
}
