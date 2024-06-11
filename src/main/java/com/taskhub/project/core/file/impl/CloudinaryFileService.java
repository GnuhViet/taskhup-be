package com.taskhub.project.core.file.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.taskhub.project.common.service.model.ServiceResult;
import com.taskhub.project.core.file.FileInfoRepo;
import com.taskhub.project.core.file.FileService;
import com.taskhub.project.core.file.domain.FileInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CloudinaryFileService implements FileService {
    private final Cloudinary cloudinary;
    private final FileInfoRepo fileInfoRepo;

    public CompletableFuture<ServiceResult<?>> uploadFileAsync(MultipartFile file) {
        return CompletableFuture.supplyAsync(() -> uploadFile(file));
    }

    private final static Map DEFAULT_PARAMS = ObjectUtils.asMap(
            "resource_type", "auto"
    );

    @Transactional
    public ServiceResult<?> uploadFile(MultipartFile file) {
        try {
            Map data = cloudinary.uploader().upload(file.getBytes(), DEFAULT_PARAMS);
            FileInfo fileInfo = FileInfo.builder()
                    .id((String) data.get("public_id"))
                    .signature((String) data.get("signature"))
                    .format((String) data.get("format"))
                    .resourceType((String) data.get("resource_type"))
                    .url((String) data.get("url"))
                    .originFileName(file.getOriginalFilename())
                    .fileSize(file.getSize())
                    .build();

            fileInfoRepo.save(fileInfo);

            return ServiceResult.ok(fileInfo);
        } catch (Exception e) {
            return ServiceResult.error("Failed to upload file: " + e.getMessage());
        }
    }

    @Transactional
    public ServiceResult<?> deleteFile(String id) {
        try {
            var fileInfo = fileInfoRepo.findById(id).orElse(null);
            if (fileInfo == null) {
                return ServiceResult.error("File not found");
            }

            Map data = cloudinary.uploader().destroy(id, ObjectUtils.asMap(
                    "resource_type", fileInfo.getResourceType()
            ));

            if (!data.get("result").equals("ok")) {
                return ServiceResult.error("failed");
            }

            fileInfoRepo.deleteById(id);
            return ServiceResult.ok("success");
        } catch (Exception e) {
            return ServiceResult.error("Failed to delete file: " + e.getMessage());
        }
    }

    @Transactional
    public ServiceResult<?> deleteFile(List<String> id) {
        try {
            var fileInfo = fileInfoRepo.findByListId(id);
            if (fileInfo == null) {
                return ServiceResult.error("File not found");
            }

            Map<String, List<FileInfo>> mapByResourceType = fileInfo.stream()
                    .collect(Collectors.groupingBy(FileInfo::getResourceType));

            for (Map.Entry<String, List<FileInfo>> entry : mapByResourceType.entrySet()) {
                List<String> listId = entry.getValue().stream().map(FileInfo::getId).collect(Collectors.toList());

                Map data = cloudinary.api().deleteResources(listId, ObjectUtils.asMap(
                        "resource_type", entry.getKey()
                ));

                if (((Map) data.get("deleted")).size() != listId.size()) { // result
                    return ServiceResult.error("failed");
                }
            }

            fileInfoRepo.deleteAll(fileInfo);
            return ServiceResult.ok("success");
        } catch (Exception e) {
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
