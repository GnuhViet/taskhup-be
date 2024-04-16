package com.taskhub.project.core.invite.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "invite_link")
public class InviteLink {
    @Id
    private String id;
    private String type;
    private String destinationId;
    private String createBy;
    private LocalDateTime createDate;
    private LocalDateTime expireDate;
}
