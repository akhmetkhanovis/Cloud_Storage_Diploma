package ru.netology.cloudstorage.service;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ru.netology.cloudstorage.entity.FileEntity;

import java.io.IOException;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public void postFile(String filename, byte[] fileBytes, long fileSize) throws IOException {

    }

    @Override
    public void deleteFile(String filename) throws IOException {

    }

    @Override
    public Resource getFile(String filename) throws IOException {
        return null;
    }

    @Override
    public List<FileEntity> getFilesList(int limit) {
        return null;
    }

    @Override
    public void renameFile(String filename, String newFileName) throws IOException {

    }
}
