package com.taskhub.project.core.invite;

import com.taskhub.project.core.invite.domain.InviteLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InviteLinkRepo extends JpaRepository<InviteLink, String> {
}
