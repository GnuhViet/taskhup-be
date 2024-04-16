package com.taskhub.project.core.user.repo;

import com.taskhub.project.core.user.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// @Repository
public interface UserRepo extends JpaRepository<AppUser, String> {
    @Query("SELECT u.id as id FROM AppUser u WHERE u.username = :username")
    AppUser.AppUserId getIdByUsername(@Param("username") String username);

    @Query("SELECT u.email as email FROM AppUser u WHERE u.id = :id")
    AppUser.AppUserEmail getEmailById(@Param("id") String id);

    AppUser findByUsername(String username);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM AppUser u WHERE u.username = :username")
    boolean existByUsername(String username);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM AppUser u WHERE u.email = :email")
    boolean existByEmail(String email);
}
