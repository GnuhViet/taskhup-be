package com.taskhub.project.core.user.entities;

import com.taskhub.project.core.board.domain.Board;
import com.taskhub.project.core.user.constans.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String email;
    private String fullName;
    private String password;
    private String status = UserStatus.Constants.ACTIVE;
    @ColumnDefault("0")
    private Boolean verify;
    @Column(unique = true)
    private String phoneNumber;

    private String avatar;

    @Column(columnDefinition = "TEXT")
    private String bio;

    // @ManyToOne(fetch =  FetchType.LAZY)
    // @JoinColumn(name = "role_id")
    // private Role roles;

    // @ManyToMany(fetch = FetchType.LAZY)
    // @JoinTable(
    //         name = "board_member",
    //         joinColumns = @JoinColumn(name = "user_id"),
    //         inverseJoinColumns = @JoinColumn(name = "board_id")
    // )
    // private Set<Board> boards;

    // projections

    public interface AppUserFullName {
        String getFullName();
    }

    public interface AppUserId {
        String getId();
    }

    public interface AppUserEmail {
        String getEmail();
    }

    public interface AppUserInfo {
        String getUsername();
        String getEmail();
        String getFullName();
        String getPhoneNumber();
        String getBio();
        String getAvatar();
        Boolean getVerify();
    }

    public interface AppUserEmailInfo {
        String getEmail();
        Boolean getConfirmStatus();
        Boolean getVerify();
    }
}

