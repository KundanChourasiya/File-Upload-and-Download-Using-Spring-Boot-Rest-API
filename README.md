# File-Upload-and-Download-Using-Spring-Boot-Rest-API

> [!NOTE]
> ### In this Api we upload and download image/ pdf files from local directory.
> 1. Postman for testing endpoint
> 2. For Database we will use Mysql
> 3. Good interet connection to build project faster

## Tech Stack
- Java-17
- Spring Boot-3
- Spring Data JPA
- lombok
- MySQL
- Postman

## Modules
* File Upload Module
* File Download Module

## Installation & Run
Before running the API server, you should update the database config inside the application.properties file.
Update the port number, username and password as per your local database config and storage file path configuration.
    
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mydb;
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=root

# image/pdf size and path configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
image.path=image/
pdf.path=pdf/
```

## API Root Endpoint

```
https://localhost:8080/
user this data for checking purpose.
```
## Step To Be Followed
> 1. Create Rest Api will return to FileData Details.
>    
>    **Project Documentation**
>    - **Entity** - FileData (class)
>    - **Payload** - ApiResponse (class)
>    - **Repository** - FileDataRepository (interface)
>    - **Service** - FileService (interface), FileServiceImpl (class)
>    - **Controller** - FileController (Class)
>    - **Global Exception** - GlobalException(class)
>      
> 2. configure storeage folder path in application.properties file.
> 3. create File service class to upload the file.
> 4. create File Controller to use upload file and download file.
> 5. create GlobalException class to handle all runtime exception.

## Important Dependency to be used
```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
	<groupId>com.mysql</groupId>
	<artifactId>mysql-connector-j</artifactId>
	<scope>runtime</scope>
    </dependency>
    
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
```

## Configure Mysql configuration and file storage path in applcation.properties file.
```properties
spring.application.name=3.File-Upload-and-Download-Using-Spring-Boot-Rest-API

# MySql Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/mydb;
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=root

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# image/pdf size and path configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
image.path=image/
pdf.path=pdf/
```

## Create FileData class in Entity Package.
```java
@Getter
@Setter
@Entity
@Table(name = "file_data")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "url")
    private String url;

}
```

## Create FileDataRepository interface in repository package.

```java
@Repository
public interface FileDataRepository extends JpaRepository<FileData, Integer> {

    // find by name
    Optional<FileData> findByName(String filename);
}
```

## Create FileService interface and FileServiceImpl class in Service package.

### *FileService*
```java
public interface FileService {

    public FileData uploadFile(MultipartFile file) throws IOException;
}
```

### *FileServiceImpl*

```java
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
            String path = pdfPath +fileName;

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

```

### *Create FileController class inside the Controller Package.* 

```java
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(response);
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
```

##  Create ApiResponse class inside the Payload Package.

### *ApiResponse* 
```java
@Setter
@Getter
public class ApiResponse<T> {

    private boolean status;
    private String message;
    private T data;

    public ApiResponse() {
    }

    public ApiResponse(boolean status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}

```

### *Create GlobalException class  inside the GlobalException Package.* 

### *GlobalException* 

```java
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
```


### Following pictures will help to understand flow of API

### *PostMan Test Cases*

Url - http://localhost:8080/file/upload

![image](https://github.com/user-attachments/assets/8f220429-6dfc-4ac5-9c55-cc1ba2da9a6f)

Url - http://localhost:8080/file/image/download/ddbe6014-a3ae-4541-bc8f-d195256f423a.jpg{filename}

![image](https://github.com/user-attachments/assets/0cf14312-6144-42b7-9a6b-b53a798be1a2)

Url - http://localhost:8080/file/upload

![image](https://github.com/user-attachments/assets/0260a6b9-55e8-473c-9e88-22e87afbdbb3)

Url - http://localhost:8080/file/pdf/download/0ee8b8d9-cf2a-486c-965a-ee79cbf07183.pdf{filename}

![image](https://github.com/user-attachments/assets/5ebdc580-510d-49a7-9b46-0ce25272cf40)


### File Storage Directory

![image](https://github.com/user-attachments/assets/9b91e216-d3cd-43a3-bf2e-e86ff8547343)


