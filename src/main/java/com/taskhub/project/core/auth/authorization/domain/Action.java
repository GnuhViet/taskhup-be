package com.taskhub.project.core.auth.authorization.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "action")
public class Action {
    @Id
    private String id;
    private String name;
    private String code;
}
