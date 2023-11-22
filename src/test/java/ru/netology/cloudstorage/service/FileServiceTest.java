package ru.netology.cloudstorage.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import ru.netology.cloudstorage.entity.FileEntity;
import ru.netology.cloudstorage.repository.FileRepository;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext
@Transactional
class FileServiceTest {
    private final String FILE_NAME = "testFile.txt";
    private final String FILE_PATH = "src/test/resources/testFile.txt";
    private final File TEST_FILE = new File(FILE_PATH);

    @Autowired
    private FileService fileService;

    @Autowired
    private FileRepository fileRepository;

    @MockBean
    private SecurityContext securityContext;

    @BeforeEach
    void securitySetup() {
        Authentication authMock = Mockito.mock(Authentication.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authMock);
        Mockito.when(authMock.getName()).thenReturn("user");
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void clearDb() {
        fileRepository.removeFileByFilenameAndFileOwner(FILE_NAME, "user");
        fileRepository.removeFileByFilenameAndFileOwner("newName.txt", "user");
    }

    @Test
    void uploadFile() throws IOException {
        // when
        uploadTestFile();

        // then
        FileEntity fileFromDb = fileRepository.findByFilename(TEST_FILE.getName()).get();

        assertEquals(FILE_NAME, fileFromDb.getFilename());
        assertEquals("txt", fileFromDb.getFileType());
        assertNotNull(fileFromDb.getFileData());
    }
    
    @Test
    void filesList() throws IOException {
        // when
        uploadTestFile();
        List<FileEntity> filesList = fileService.getFilesList(10);
        
        // then
        assertEquals(1, filesList.size());
        
        FileEntity fileFromDb = filesList.get(0);
        
        assertNotNull(fileFromDb.getFileData());
        assertEquals(FILE_NAME, fileFromDb.getFilename());
        assertEquals("txt", fileFromDb.getFileType());
    }

    @Test
    void download() throws IOException {
        // when
        uploadTestFile();
        FileEntity fileFromDb = fileService.downloadFile(FILE_NAME);

        // then
        assertNotNull(fileFromDb.getFileData());
        assertEquals(FILE_NAME, fileFromDb.getFilename());
        assertEquals("txt", fileFromDb.getFileType());
    }

    @Test
    void rename() throws IOException {
        // when
        uploadTestFile();
        fileService.renameFile(FILE_NAME, "newName.txt");

        // then
        Optional<FileEntity> fileOptional = fileRepository.findByFilename("newName.txt");
        assertTrue(fileOptional.isPresent());

        FileEntity fileFromDb = fileOptional.get();
        assertEquals("newName.txt", fileFromDb.getFilename());

    }

    @Test
    void deleteFile() throws IOException {
        // when
        uploadTestFile();
        fileService.deleteFile("newName.txt");

        // then
        Optional<FileEntity> fileFromDb = fileRepository.findByFilename("newName.txt");

        assertTrue(fileFromDb.isEmpty());
    }

    private void uploadTestFile() throws IOException {
        byte[] content = Files.readAllBytes(TEST_FILE.toPath());
        fileService.uploadFile(TEST_FILE.getName(), new MockMultipartFile(FILE_NAME, FILE_NAME, "txt", content));
    }
}
