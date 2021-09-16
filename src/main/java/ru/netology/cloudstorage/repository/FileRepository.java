package ru.netology.cloudstorage.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cloudstorage.entity.FileEntity;
import ru.netology.cloudstorage.entity.UserEntity;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    List<FileEntity> findAllByUserEntity_Username(String username, Sort sort);

    void removeFileByFilenameAndUserEntity(String fileName, UserEntity userEntity);

    FileEntity findByFilenameAndUserEntity(String fileName, UserEntity userEntity);
}
