package com.taskhub.project.core.file;

import com.taskhub.project.common.service.model.ServiceResult;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    ServiceResult<?> uploadFile(MultipartFile file);
    ServiceResult<?> deleteFile(String id);
    boolean isUploadSuccess(ServiceResult<?> result);
    boolean isDeleteSuccess(ServiceResult<?> result);
}
