package com.taskhub.project.core.auth.authentication.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class AuthorResponse {
    private String authorToken;
}
