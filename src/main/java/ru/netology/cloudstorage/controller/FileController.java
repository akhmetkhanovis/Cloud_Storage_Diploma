package ru.netology.cloudstorage.controller;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.model.ErrorResponse;
import ru.netology.cloudstorage.model.NewFileName;
import ru.netology.cloudstorage.service.FileService;

import java.io.IOException;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/file")
public class FileController {

    private static final String FILE_NAME = "filename";
    private static final String FILE = "file";

    private final FileService fileService;

    protected final Log logger = LogFactory.getLog(this.getClass());

    @PostMapping()
    public ResponseEntity<?> postFile(@RequestParam(FILE_NAME) String filename,
                                      @RequestPart(FILE) @NotNull MultipartFile file) {
        try {
            fileService.postFile(filename, file.getBytes(), file.getSize());
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), 400));
        }
        return ResponseEntity.ok().body(null);
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteFile(@RequestParam(FILE_NAME) String filename) {
        try {
            fileService.deleteFile(filename);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), 400));
        }
        return ResponseEntity.ok().body(null);
    }

    @GetMapping(produces = MediaType.ALL_VALUE)
    public ResponseEntity<?> getFile(@RequestParam(FILE_NAME) String filename) {
        Resource resource = null;
//        try {
//            resource = fileService.getFile(filename);
//            logger.info("getFile.fileSize=" + resource.getFile().length());
            return ResponseEntity.ok().body("Hello " + filename);
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION,
//                            "attachment; filename=\"" + resource.getFilename() + "\"")
//                    .contentLength(resource.getFile().length())
//                    .body(resource);
//        } catch (IOException e) {
//            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), 400));
//        }
    }

    @PutMapping()
    public ResponseEntity<?> updateFile(@RequestParam(FILE_NAME) String filename, @RequestBody NewFileName newFileName) {
        try {
            fileService.renameFile(filename, newFileName.getFilename());
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), 400));
        }
        return ResponseEntity.ok().body(null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternalServerErrors(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage(), 500));
    }
}
