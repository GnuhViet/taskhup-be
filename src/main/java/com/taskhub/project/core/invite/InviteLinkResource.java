package com.taskhub.project.core.invite;

import com.taskhub.project.comon.service.model.ServiceResult;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/invite")
@AllArgsConstructor
public class InviteLinkResource {
    private final InviteLinkRepo inviteLinkRepo;

    @GetMapping("/{id}")
    private ServiceResult<?> acceptInvite(Principal principal, @PathVariable String id) {
        return null;
    }


    @GetMapping("/create")
    private ServiceResult<?> createInvite(Principal principal) {
        return null;
    }
}
