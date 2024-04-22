package com.taskhub.project.core.invite.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "invite_link")
public class InviteLink {
    @Id
    private String id;
    private String type; // workspace //board
    private String destinationId;
    private String createBy;
    private LocalDateTime createDate;
    private LocalDateTime expireDate;
}
