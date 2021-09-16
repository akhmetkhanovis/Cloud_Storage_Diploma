package ru.netology.cloudstorage.service;

import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.entity.FileEntity;

import java.io.IOException;
import java.util.List;

public interface FileService {

    public FileEntity uploadFile(String fileName, MultipartFile file) throws IOException;

    public FileEntity downloadFile(String fileName);

    public void deleteFile(String filename) throws IOException;

    public List<FileEntity> getFilesList(int limit);

    void renameFile(String filename, String newFileName) throws IOException;
}
