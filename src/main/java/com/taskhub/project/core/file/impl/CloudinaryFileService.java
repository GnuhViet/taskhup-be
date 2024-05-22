package com.taskhub.project.core.file.impl;

import com.cloudinary.Cloudinary;
import com.taskhub.project.common.service.model.ServiceResult;
import com.taskhub.project.core.file.FileInfoRepo;
import com.taskhub.project.core.file.FileService;
import com.taskhub.project.core.file.domain.FileInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryFileService implements FileService {
    private final Cloudinary cloudinary;
    private final FileInfoRepo fileInfoRepo;

    @Transactional
    public ServiceResult<?> uploadFile(MultipartFile file) {
        try {
            Map data = cloudinary.uploader().upload(file.getBytes(), Map.of());
            FileInfo fileInfo = FileInfo.builder()
                    .id((String) data.get("public_id"))
                    .signature((String) data.get("signature"))
                    .format((String) data.get("format"))
                    .resourceType((String) data.get("resource_type"))
                    .url((String) data.get("url"))
                    .build();

            fileInfoRepo.save(fileInfo);

            return ServiceResult.ok(fileInfo);
        } catch (IOException e) {
            return ServiceResult.error("Failed to upload file: " + e.getMessage());
        }
    }

    @Transactional
    public ServiceResult<?> deleteFile(String id) {
        try {
            Map data = cloudinary.uploader().destroy(id, Map.of());

            if (!data.get("result").equals("ok")) {
                return ServiceResult.error("failed");
            }

            fileInfoRepo.deleteById(id);
            return ServiceResult.ok("success");
        } catch (IOException e) {
            return ServiceResult.error("Failed to delete file: " + e.getMessage());
        }
    }

    @Override
    public boolean isUploadSuccess(ServiceResult<?> result) {
        return !(result.getData() instanceof String);
    }

    @Override
    public boolean isDeleteSuccess(ServiceResult<?> result) {
        var data = (String) result.getData();
        return data.equals("success");
    }
}
