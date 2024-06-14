package com.taskhub.project.core.user;

import com.taskhub.project.aspect.exception.model.ErrorsData;
import com.taskhub.project.common.Constants;
import com.taskhub.project.common.service.model.ServiceResult;
import com.taskhub.project.core.auth.authentication.dtos.UpdateInfoRequest;
import com.taskhub.project.core.auth.authorization.constans.DefaultFile;
import com.taskhub.project.core.board.service.HistoryService;
import com.taskhub.project.core.file.FileService;
import com.taskhub.project.core.file.domain.FileInfo;
import com.taskhub.project.core.helper.validator.ValidatorService;
import com.taskhub.project.core.user.entities.AppUser;
import com.taskhub.project.core.user.entities.UserNotificationRead;
import com.taskhub.project.core.user.entities.UserNotificationReadKey;
import com.taskhub.project.core.user.model.MarkAsReadNotificationReq;
import com.taskhub.project.core.user.model.NotificationReq;
import com.taskhub.project.core.user.repo.UserNotificationReadRepo;
import com.taskhub.project.core.user.repo.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@Slf4j
public class AppUserService {
    private final UserRepo userRepo;
    private final FileService fileService;
    private final HistoryService historyService;
    private final ValidatorService validator;
    private final UserNotificationReadRepo userNotificationReadRepo;

    public AppUserService(
            UserRepo userRepo,
            @Qualifier("cloudinaryFileService") FileService fileService,
            ValidatorService validator,
            HistoryService historyService,
            UserNotificationReadRepo userNotificationReadRepo
    ) {
        this.userRepo = userRepo;
        this.fileService = fileService;
        this.validator = validator;
        this.historyService = historyService;
        this.userNotificationReadRepo = userNotificationReadRepo;
    }

    public ServiceResult<?> getUserInfo(String id) {
        return ServiceResult.ok(userRepo.getUserInfo(id));
    }

    public ServiceResult<?> getUserEmailInfo(String id) {
        return ServiceResult.ok(userRepo.getUserEmailInfo(id));
    }

    public ServiceResult<?> updateAvatar(String id, MultipartFile file) {
        var user = userRepo.findById(id).orElse(null);

        if (user == null) {
            return ServiceResult.error("User not found");
        }

        var oldAvatar = user.getAvatar();
        if (!DefaultFile.isDefaultFile(oldAvatar)) {
            var resp = fileService.deleteFile(oldAvatar);
            if (!fileService.isDeleteSuccess(resp)) {
                return ServiceResult.error("Internal server error: Failed to delete old avatar");
            }
        }

        var fileInfo = fileService.uploadFile(file);

        if (!fileService.isUploadSuccess(fileInfo)) {
            return ServiceResult.error("Internal server error: Failed to upload avatar");
        }

        user.setAvatar(((FileInfo) fileInfo.getData()).getId());

        userRepo.save(user);
        return ServiceResult.ok("Avatar updated successfully");
    }

    public ServiceResult<?> updateUserInfo(String id, UpdateInfoRequest request) {
        final AppUser[] userDb = new AppUser[1];
        validator.tryValidate(request)
                .withConstraint(
                        () -> {
                            userDb[0] = userRepo.findById(id).orElse(null);
                            return userDb[0] == null;
                        },
                        ErrorsData.of("User not found", "04", id)
                )
                .withConstraint(
                        () -> {
                            if (StringUtils.isBlank(request.getEmail())) {
                                return false;
                            }

                            if (request.getEmail().equals(userDb[0].getEmail())) {
                                return false;
                            }

                            return userRepo.existByEmail(request.getEmail());
                        },
                        ErrorsData.of("Email already exists", "05", request.getEmail())
                )
                .throwIfFails();

        var user = userDb[0];

        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setBio(request.getBio());

        if (StringUtils.isNotBlank(request.getEmail())) {
            user.setEmail(request.getEmail());
            user.setVerify(false);
        }

        userRepo.save(user);

        return ServiceResult.ok("User info updated successfully");
    }

    public ServiceResult<?> getNotifications(NotificationReq req, String userId) {
        if (req.getIsOnlyUnread() == null) {
            return ServiceResult.error("Invalid request");
        }

        return ServiceResult
                .ok(historyService.getUserNotification(userId, req.getIsOnlyUnread()));
    }

    public ServiceResult<?> markAsReadNotification(MarkAsReadNotificationReq request, String userId) {
        if (request.getNotificationIds() == null || request.getNotificationIds().isEmpty()) {
            return ServiceResult.error("Invalid request");
        }

        for (var id : request.getNotificationIds()) {
            var key = new UserNotificationReadKey(userId, id);

            if (userNotificationReadRepo.existsById(key)) {
                continue;
            }
            userNotificationReadRepo.save(
                    UserNotificationRead.builder()
                            .id(key)
                            .build()
            );
        }

        return ServiceResult.ok(Constants.ServiceStatus.SUCCESS);

    }
}
