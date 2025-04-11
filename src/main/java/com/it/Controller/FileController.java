package com.it.Controller;

import com.it.Entity.FileData;
import com.it.Payload.ApiResponse;
import com.it.Repository.FileDataRepository;
import com.it.Service.FileService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@RequestMapping("/file")
@AllArgsConstructor
public class FileController {

    private FileService service;

    @Autowired
    private FileDataRepository repository;

    // upload file Controller
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<?>> uploadFile(@RequestParam ("file") MultipartFile file) throws IOException {
        if (file.isEmpty()){
            ApiResponse<Object> response = new ApiResponse<>(false, "Request must contain file", null);
            return ResponseEntity.status(HttpStatus.OK.value()).body(response);
        }
        FileData fileData = service.uploadFile(file);
        ApiResponse<Object> response = new ApiResponse<>(true, "File upload successfully", fileData.getUrl());
        return ResponseEntity.status(HttpStatus.OK.value()).body(response);
    }

    // this method to download all only png media types
//    @GetMapping("/image/download/{filename}")
//    public ResponseEntity<Object> downloadImageFile(@PathVariable String filename) throws IOException {
//        Optional<FileData> fileData = repository.findByName(filename);
//        String filePath = fileData.get().getUrl();
//        byte[] file = Files.readAllBytes(new File(filePath).toPath());
//        return ResponseEntity.status(HttpStatus.OK)
//                .contentType(MediaType.valueOf("image/png"))
//                .body(file);
//    }

    // this method to download all image media types
    @GetMapping("/image/download/{filename}")
    public ResponseEntity<Object> downloadImageAllMediaFiles(@PathVariable String filename) throws IOException {
        Optional<FileData> fileData = repository.findByName(filename);
        String filePath = fileData.get().getUrl();
        File file = new File(filePath);
        byte[] fileBytes = Files.readAllBytes(file.toPath());

        String mediaFileTypes = Files.probeContentType(file.toPath());
        if (mediaFileTypes == null) {
            mediaFileTypes = MediaType.APPLICATION_OCTET_STREAM_VALUE; // fallback
        }

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType(mediaFileTypes))
                .body(fileBytes);
    }

    // this method to download pdf files
    @GetMapping("/pdf/download/{filename}")
    public ResponseEntity<Object> downloadPdfFile(@PathVariable String filename) throws IOException {
        Optional<FileData> fileData = repository.findByName(filename);
        String filePath = fileData.get().getUrl();
        byte[] file = Files.readAllBytes(new File(filePath).toPath());
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("application/pdf"))
                .body(file);
    }
}
