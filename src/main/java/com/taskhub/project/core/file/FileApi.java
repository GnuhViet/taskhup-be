package com.taskhub.project.core.file;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/file")
public class FileApi {

    private final FileService fileService;

    public FileApi(
            @Qualifier("cloudinaryFileService") FileService fileService
    ) {
        this.fileService = fileService;
    }

    // @GetMapping("/server")
    // public ResponseEntity<?> getServer() {
    //     return ResponseEntity.ok(fileService.getServer());
    // }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(fileService.uploadFile(file));
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable String id) {
        return ResponseEntity.ok(fileService.deleteFile(id));
    }

    public void downloadFile() {
        // download file
    }
}
