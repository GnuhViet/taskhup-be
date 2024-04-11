package com.taskhub.project.core.user;

import com.taskhub.project.aspect.exception.ServerException;
import com.taskhub.project.core.authentication.dtos.DetailsAppUserDTO;
import com.taskhub.project.core.authentication.model.UserProfileRequest;
import com.taskhub.project.core.user.constans.UserStatus;
import com.taskhub.project.core.user.entities.AppUser;
import com.taskhub.project.core.user.repo.RoleRepo;
import com.taskhub.project.core.user.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Objects.requireNonNull(username, "Username must not be null");

        var user = userRepo.findByUsername(username);

        if (user == null) {
            throw new ServerException("UserService.loadUserByUsername: User not found in the database");
        }

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        // user.getRoles().forEach(role -> {
        //     authorities.add(new SimpleGrantedAuthority(role.getName()));
        // });
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
}
