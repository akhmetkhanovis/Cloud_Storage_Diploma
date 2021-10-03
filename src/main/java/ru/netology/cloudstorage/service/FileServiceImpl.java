package ru.netology.cloudstorage.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.entity.FileEntity;
import ru.netology.cloudstorage.repository.FileRepository;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    @Override
    @Transactional
    public FileEntity uploadFile(String filename, MultipartFile file) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            FileEntity fileEntity = FileEntity.builder()
                    .filename(filename)
                    .fileType(file.getContentType())
                    .fileData(file.getBytes())
                    .fileSize(file.getSize())
                    .fileOwner(username)
                    .build();
            return fileRepository.save(fileEntity);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @Transactional
    public FileEntity downloadFile(String filename) {
        return fileRepository.findByFilenameAndFileOwner(filename, getUsernameFromContext());
    }

    @Override
    @Transactional
    public void deleteFile(String filename) {
        fileRepository.removeFileByFilenameAndFileOwner(filename, getUsernameFromContext());
    }


    @Override
    public List<FileEntity> getFilesList(int limit) {
        return fileRepository.findAllByFileOwner(getUsernameFromContext(), Sort.by("filename"));
    }

    @Override
    public void renameFile(String filename, String newFileName) {
        FileEntity fileEntity = fileRepository.findByFilenameAndFileOwner(filename, getUsernameFromContext());
        fileEntity.setFilename(newFileName);
        fileRepository.saveAndFlush(fileEntity);
    }

    private String getUsernameFromContext() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
