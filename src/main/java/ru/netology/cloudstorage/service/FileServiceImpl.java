package ru.netology.cloudstorage.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.entity.FileEntity;
import ru.netology.cloudstorage.entity.UserEntity;
import ru.netology.cloudstorage.repository.FileRepository;
import ru.netology.cloudstorage.repository.UserRepository;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Service
@AllArgsConstructor
public class FileServiceImpl implements FileService {

    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    @Override
    @Transactional
    public FileEntity uploadFile(String fileName, MultipartFile file) {
        try {
            FileEntity fileEntity = FileEntity.builder()
                    .filename(fileName)
                    .type(file.getContentType())
                    .fileData(file.getBytes())
                    .size(file.getSize())
                    .userEntity(getUserFromContext())
                    .build();
            return fileRepository.save(fileEntity);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @Transactional
    public FileEntity downloadFile(String fileName) {
        return fileRepository.findByFilenameAndUserEntity(fileName, getUserFromContext());
    }

    @Override
    @Transactional
    public void deleteFile(String filename) {
        UserEntity user = getUserFromContext();
        fileRepository.removeFileByFilenameAndUserEntity(filename, getUserFromContext());
    }


    @Override
    public List<FileEntity> getFilesList(int limit) {
        UserEntity user = getUserFromContext();
        return fileRepository.findAllByUserEntity_Username(user.getUsername(), Sort.by("filename"));
    }

    @Override
    public void renameFile(String filename, String newFileName) {
        FileEntity fileEntity = fileRepository.findByFilenameAndUserEntity(filename, getUserFromContext());
        fileEntity.setFilename(newFileName);
        fileRepository.saveAndFlush(fileEntity);
    }

    private UserEntity getUserFromContext() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
