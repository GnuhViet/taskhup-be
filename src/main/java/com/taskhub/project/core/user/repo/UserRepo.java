package com.taskhub.project.core.user.repo;

import com.taskhub.project.core.user.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

// @Repository
public interface UserRepo extends JpaRepository<AppUser, String> {
    @Query("SELECT u.fullName as fullName FROM AppUser u WHERE u.id = :id")
    AppUser.AppUserFullName getFullNameById(@Param("id") String id);

    @Query("SELECT u.id as id FROM AppUser u WHERE u.username = :username")
    AppUser.AppUserId getIdByUsername(@Param("username") String username);

    @Query("SELECT u.email as email FROM AppUser u WHERE u.id = :id")
    AppUser.AppUserEmail getEmailById(@Param("id") String id);

    AppUser findByUsername(String username);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM AppUser u WHERE u.username = :username")
    boolean existByUsername(String username);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM AppUser u WHERE u.email = :email")
    boolean existByEmail(String email);

    @Query(value = """
    select
        au.id as `id`,
        au.username as `username`,
        au.email as `email`,
        au.full_name as `fullName`,
        au.phone_number as `phoneNumber`,
        au.bio as `bio`,
        fi.url as `avatar`,
        au.verify as `verify`
    from app_user au
        join file_info fi on au.avatar = fi.id
    where au.id = :id
    """, nativeQuery = true
    )
    AppUser.AppUserInfo getUserInfo(String id);

    @Query(value = """
    select
        au.email as `email`,
        au.verify as `verify`,
        IF(ct.id is null, 'false', 'true') as `confirmStatus`
    from app_user au
        join file_info fi on au.avatar = fi.id
        left join confirm_token ct on au.id = ct.app_user_id
    where au.id = :id
    """, nativeQuery = true
    )
    AppUser.AppUserEmailInfo getUserEmailInfo(String id);
}
