package com.it.GlobalException;

import com.it.Payload.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import java.io.IOException;

@RestControllerAdvice
public class GlobalException {

    // Global Exception handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> ExceptionHandler(Exception ex){
        ApiResponse<Object> response = new ApiResponse<>(false, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(response);
    }

    // MaxUploadSizeExceededException handler
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Object>> MaxUploadSizeExceededExceptionHandler(MaxUploadSizeExceededException ex){
        ApiResponse<Object> response = new ApiResponse<>(false, "File too large! Maximum allowed size exceeded.", null);
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE.value()).body(response);
    }

    // MultipartExceptionException Handler
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ApiResponse<Object>> MultipartExceptionExceptionHandler(MultipartException ex){
        ApiResponse<Object> response = new ApiResponse<>(false, "Invalid multipart request or corrupted file.", null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(response);
    }

    // IOException Handler
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiResponse<Object>> IOExceptionHandler(IOException ex){
        ApiResponse<Object> response = new ApiResponse<>(false, "I/O error occurred while processing the file.", null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(response);
    }

    // IllegalStateException Handler
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Object>> IllegalStateExceptionHandler(IllegalStateException ex){
        ApiResponse<Object> response = new ApiResponse<>(false, "Illegal state: file upload may be missing multipart configuration.", null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(response);
    }

}
