package com.taskhub.project.core.file.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UploadResponse {

    private Data data;
    private String status;

    // getters and setters

    @lombok.Data
    public static class Data {
        private String code;
        @JsonProperty("downloadPage")
        private String downloadPage;
        @JsonProperty("fileId")
        private String fileId;
        @JsonProperty("fileName")
        private String fileName;
        private String md5;
        @JsonProperty("parentFolder")
        private String parentFolder;
    }
}
