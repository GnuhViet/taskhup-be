package com.taskhub.project.core.invite;

import com.taskhub.project.core.invite.domain.InviteLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InviteLinkRepo extends JpaRepository<InviteLink, String> {

    @Query("""
            SELECT CASE WHEN COUNT(i) > 0 THEN TRUE ELSE FALSE END
            FROM InviteLink i
            WHERE i.destinationId = :destinationId
            and i.expireDate > CURRENT_TIMESTAMP
    """)
    boolean existsByDestinationId(String destinationId);


    @Query("""
            SELECT i
            FROM InviteLink i
            WHERE i.destinationId = :destinationId
            and i.expireDate > CURRENT_TIMESTAMP
    """)
    InviteLink findByDestinationId(String destinationId);
}
