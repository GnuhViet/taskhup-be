package com.taskhub.project.core.workspace;

import com.taskhub.project.comon.service.model.ServiceResult;
import com.taskhub.project.core.helper.validator.ValidatorService;
import com.taskhub.project.core.workspace.domain.WorkSpace;
import com.taskhub.project.core.workspace.dto.WorkSpaceDto;
import com.taskhub.project.core.workspace.model.GetWorkSpaceResp;
import com.taskhub.project.core.workspace.model.WorkSpaceCreateReq;
import com.taskhub.project.core.workspace.model.WorkSpaceCreateResp;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class WorkSpaceService {
    private final ValidatorService validator;
    private final ModelMapper mapper;

    private final WorkSpaceRepo workSpaceRepo;

    @Valid
    public ServiceResult<WorkSpaceCreateResp> createWorkSpace(WorkSpaceCreateReq request, String userId) {
        validator.tryValidate(request).throwIfFails();

        var resp =  workSpaceRepo.save(mapper.map(request, WorkSpace.class));

        resp.setCreateBy(userId);
        resp.setOwnerId(userId);
        resp.setCreateDate(LocalDateTime.now());

        return ServiceResult.created(mapper.map(resp, WorkSpaceCreateResp.class));
    }

    public ServiceResult<GetWorkSpaceResp> getWorkSpace(String userId) {
        List<WorkSpace.UserWorkSpace> userWorkSpaceList = workSpaceRepo.getUserWorkSpaces(userId).orElse(null);

        if (userWorkSpaceList == null) {
            return ServiceResult.notFound();// TODO
        }

        GetWorkSpaceResp resp = new GetWorkSpaceResp();

        userWorkSpaceList.forEach(item -> {
            if ("MEMBER".equals(item.getType())) {
                resp.getUserWorkSpaces().add(mapper.map(item, WorkSpaceDto.class));
            }
            else if ("GUEST".equals(item.getType())) {
                resp.getGuestWorkSpaces().add(mapper.map(item, WorkSpaceDto.class));
            }
        });

        return ServiceResult.ok(resp);
    }
}
