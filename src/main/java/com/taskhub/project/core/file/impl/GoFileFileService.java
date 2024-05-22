package com.taskhub.project.core.file.impl;

import com.cloudinary.Cloudinary;
import com.taskhub.project.common.service.model.ServiceResult;
import com.taskhub.project.core.file.FileService;
import com.taskhub.project.core.file.model.UploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class GoFileFileService implements FileService {
    private static final String SERVER_LIST_API = "https://api.gofile.io/servers";
    private static final String UPLOAD_URL = "https://store1.gofile.io/contents/uploadfile";
    private static final String TOKEN = "Bearer DaVz5aQoHTYlOZPvVl9QO2CILKwjtGh5";
    private static final String FOLDER_ID = "7be058c9-317d-41f4-922b-174f58c4789e";
    private static final HttpHeaders UPLOAD_FILE_HEADERS;
    static {
        UPLOAD_FILE_HEADERS = new HttpHeaders();
        UPLOAD_FILE_HEADERS.setContentType(MediaType.MULTIPART_FORM_DATA);
        UPLOAD_FILE_HEADERS.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        UPLOAD_FILE_HEADERS.set("Authorization", TOKEN);
    }

    public ServiceResult<Object> getServer() {
        var restTemplate = new RestTemplate();
        var result = restTemplate.getForObject(SERVER_LIST_API, Object.class);
        return ServiceResult.ok(result);
    }

    public ServiceResult<?> uploadFile(MultipartFile file) {
        var restTemplate = new RestTemplate();
        var fileMap = new LinkedMultiValueMap<String, String>();
        var contentDisposition = ContentDisposition
                .builder("form-data")
                .name("file")
                .filename(file.getOriginalFilename()) // fix this
                .build();

        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
        fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
        try {

            var fileEntity = new HttpEntity<byte[]>(file.getBytes(), fileMap);
            var body = new LinkedMultiValueMap<String, Object>();

            body.add("file", fileEntity);
            body.add("folderId", FOLDER_ID);

            var requestEntity = new HttpEntity<MultiValueMap<String, Object>>(body, UPLOAD_FILE_HEADERS);

            ResponseEntity<UploadResponse> response = restTemplate.exchange(
                    UPLOAD_URL,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return ServiceResult.ok(response.getBody());
            } else {
                throw new RuntimeException("Failed to upload file: " + response.getBody());
            }
        } catch (IOException e) {
           return ServiceResult.error("Failed to upload file: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<?> deleteFile(String id) {
        return null;
    }

    @Override
    public boolean isUploadSuccess(ServiceResult<?> result) {
        return false;
    }

    @Override
    public boolean isDeleteSuccess(ServiceResult<?> result) {
        return false;
    }

    public void downloadFile() {
        // download file
    }
}
