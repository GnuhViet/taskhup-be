package com.taskhub.project.core.auth.authentication.repo;

import com.taskhub.project.core.auth.authentication.entities.ConfirmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ConfirmTokenRepo extends JpaRepository<ConfirmToken, String> {
    Optional<ConfirmToken> findByToken(String token);

    @Transactional
    @Query(
            value = "select * from confirm_token where APP_USER_ID = :userId",
            nativeQuery = true
    )
    Optional<ConfirmToken> findByAppUser_Id(@Param("userId") String userId);
}
