package ru.netology.cloudstorage.service;

import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.entity.FileEntity;

import java.io.IOException;
import java.util.List;

public interface FileService {

    void uploadFile(String fileName, MultipartFile file) throws IOException;

    FileEntity downloadFile(String fileName);

    void deleteFile(String filename) throws IOException;

    List<FileEntity> getFilesList(int limit);

    void renameFile(String filename, String newFileName) throws IOException;
}
