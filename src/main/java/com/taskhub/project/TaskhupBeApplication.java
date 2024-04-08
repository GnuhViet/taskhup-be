package com.taskhub.project;

import com.taskhub.project.board.domain.Board;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskhupBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskhupBeApplication.class, args);

        Board board = new Board();
    }

}
