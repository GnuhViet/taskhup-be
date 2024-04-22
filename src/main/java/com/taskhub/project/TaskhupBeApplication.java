package com.taskhub.project;

import com.taskhub.project.core.board.domain.Board;
import com.taskhub.project.core.user.UserService;
import com.taskhub.project.core.user.constans.UserStatus;
import com.taskhub.project.core.user.entities.AppUser;
import com.taskhub.project.core.user.repo.UserRepo;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
public class TaskhupBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskhupBeApplication.class, args);

        Board board = new Board();
    }

    private final PasswordEncoder passwordEncoder;

    @Bean
    @Transactional
    CommandLineRunner run(UserRepo userRepo
                          ) {
        return args -> {

            if (userRepo.existByUsername("string")) {
                log.info("Finish test data initial");
                return;
            }

            userRepo.save(AppUser.builder()
                    .username("string")
                    .password(passwordEncoder.encode("string"))
                    .status(UserStatus.Active.name())
                    .build());

            log.info("Finish test data initial");
        };
    }
}
