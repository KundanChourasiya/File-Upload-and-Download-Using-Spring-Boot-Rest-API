package com.it.Service;

import com.it.Entity.FileData;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    public FileData uploadFile(MultipartFile file) throws IOException;
}
