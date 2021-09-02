package ru.netology.cloudstorage.service;

import org.springframework.core.io.Resource;
import ru.netology.cloudstorage.entity.FileEntity;

import java.io.IOException;
import java.util.List;

public interface FileService {

    void postFile(String filename, byte[] fileBytes, long fileSize) throws IOException;

    void deleteFile(String filename) throws IOException;

    Resource getFile(String filename) throws IOException;

    List<FileEntity> getFilesList(int limit);

    void renameFile(String filename, String newFileName) throws IOException;
}
