package com.it.Service.Impl;

import com.it.Entity.FileData;
import com.it.Repository.FileDataRepository;
import com.it.Service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileDataRepository repository;

    // load path from Application.properties file
    @Value("${image.path}")
    private String imagePath;

    // load path from Application.properties file
    @Value("${pdf.path}")
    private String pdfPath;


    @Override
    public FileData uploadFile(MultipartFile file) throws IOException {
        FileData fileData = null;

        // check Content type jpeg or png
        if (file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png")) {

            // create folder if not available
            File f = new File(imagePath);
            if (!f.exists()) {
                f.mkdir();
            }

            // change file name
            String originalFilename = file.getOriginalFilename();
            String randomNum = UUID.randomUUID().toString();
            String fileName = randomNum.concat(originalFilename.substring(originalFilename.lastIndexOf(".")));

            // set path where file is saved
            String path = imagePath +fileName;

            fileData = FileData.builder()
                    .name(fileName)
                    .type(file.getContentType())
                    .url(path)
                    .build();


            // save the file into path directory
            Files.copy(file.getInputStream(), Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
        }

        // check Content type pdf
        if (file.getContentType().equals("application/pdf")) {

            // create folder if not available
            File f = new File(pdfPath);
            if (!f.exists()) {
                f.mkdir();
            }

            // change file name
            String originalFilename = file.getOriginalFilename();
            String randomNum = UUID.randomUUID().toString();
            String fileName = randomNum.concat(originalFilename.substring(originalFilename.lastIndexOf(".")));

            // set path where file is saved
            String path = imagePath +fileName;

            fileData = FileData.builder()
                    .name(fileName)
                    .type(file.getContentType())
                    .url(path)
                    .build();

            // save the file into path directory
            Files.copy(file.getInputStream(), Paths.get(path), StandardCopyOption.REPLACE_EXISTING);

        }

        // save file data in database
        repository.save(fileData);

        return fileData;
    }
}
