package com.taskhub.project.core.board.service;

import com.taskhub.project.core.board.repo.BoardCardRepo;
import com.taskhub.project.core.board.repo.BoardColumnRepo;
import com.taskhub.project.core.board.repo.BoardRepo;
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
