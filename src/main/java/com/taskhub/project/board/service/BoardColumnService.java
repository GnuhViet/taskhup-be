package com.taskhub.project.board.service;

import com.taskhub.project.board.repo.BoardColumnRepo;
import com.taskhub.project.board.repo.BoardRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Transactional
public class BoardColumnService {

    private final BoardRepo boardRepo;
    private final BoardColumnRepo boardColumnRepo;
    private final ModelMapper mapper;



}
