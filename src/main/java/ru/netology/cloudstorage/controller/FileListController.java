package ru.netology.cloudstorage.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.cloudstorage.entity.FileEntity;
import ru.netology.cloudstorage.service.FileService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/list")
public class FileListController {

    private final FileService fileService;

    @GetMapping
    List<FileEntity> getFilesList(@RequestParam int limit) {
        return fileService.getFilesList(limit);
    }
}
