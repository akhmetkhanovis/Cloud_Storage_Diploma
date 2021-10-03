package ru.netology.cloudstorage.controller;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.entity.FileEntity;
import ru.netology.cloudstorage.model.ErrorResponse;
import ru.netology.cloudstorage.model.NewFileName;
import ru.netology.cloudstorage.service.FileService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping
@AllArgsConstructor
public class FileController {

    private static final String FILE_NAME = "filename";
    private static final String FILE = "file";

    private final FileService fileService;

    private static final Log logger = LogFactory.getLog(FileController.class);

    @PostMapping("/file")
    public void uploadFile(@RequestParam(FILE_NAME) String filename,
                           @RequestPart(FILE) @NotNull MultipartFile file) throws IOException {
        fileService.uploadFile(filename, file);
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestParam(FILE_NAME) String filename) {
        try {
            fileService.deleteFile(filename);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), 400));
        }
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/list")
    List<FileEntity> getFilesList(@RequestParam int limit) {
        return fileService.getFilesList(limit);
    }

    @GetMapping(path = "/file", produces = MediaType.ALL_VALUE)
    public ResponseEntity<Resource> downloadFile(@RequestParam(FILE_NAME) String filename) {
        FileEntity fileEntity = fileService.downloadFile(filename);
        logger.info("Downloading file " + fileEntity.getFilename());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileEntity.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename = " + fileEntity.getFilename())
                .body(new ByteArrayResource(fileEntity.getFileData()));
    }

    @PutMapping("/file")
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
