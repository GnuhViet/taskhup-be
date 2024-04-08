package com.taskhub.project.board.service;

import com.taskhub.project.board.repo.BoardCardRepo;
import com.taskhub.project.board.repo.BoardColumnRepo;
import com.taskhub.project.board.repo.BoardRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BoardCardService {
    private final BoardRepo boardRepo;
    private final BoardCardRepo boardCardRepo;
    private final BoardColumnRepo boardColumnRepo;
    private final ModelMapper mapper;


}
