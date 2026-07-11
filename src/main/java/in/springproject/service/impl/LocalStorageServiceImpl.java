package in.springproject.service.impl;

import in.springproject.exception.FileStorageException;
import in.springproject.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

/**
 * Local file system implementation of {@link StorageService}.
 * Files are stored under the configured base path and served via the /uploads/** mapping.
 *
 * <p><strong>Production note:</strong> Swap this bean for an S3/GCS implementation
 * by providing an alternative {@code @Primary} or {@code @Profile}-scoped bean.
 *
 * <h3>Required application properties</h3>
 * <pre>
 * app.storage.local.base-path=./uploads
 * app.base-url=http://localhost:8080
 * </pre>
 */
@Service
@Slf4j
public class LocalStorageServiceImpl implements StorageService {

    @Value("${app.storage.local.base-path:./uploads}")
    private String basePath;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    /** Ensure the root upload directory exists at startup. */
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(basePath));
            log.info("Storage initialized at: {}", Paths.get(basePath).toAbsolutePath());
        } catch (IOException e) {
            throw new FileStorageException("Could not initialize storage directory", e);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String subdirectory) {
        if (file.isEmpty()) {
            throw new FileStorageException("Cannot store empty file");
        }

        String originalFilename = StringUtils.cleanPath(
            file.getOriginalFilename() != null ? file.getOriginalFilename() : "file");
        String extension = "";
        int lastDot = originalFilename.lastIndexOf('.');
        if (lastDot > 0) {
            extension = originalFilename.substring(lastDot);
        }

        String uniqueFilename = UUID.randomUUID() + extension;

        try {
            Path targetDir = Paths.get(basePath, subdirectory);
            Files.createDirectories(targetDir);
            Path targetPath = targetDir.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Stored file: {}", targetPath);
            return "/uploads/" + subdirectory + "/" + uniqueFilename;
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file: " + originalFilename, e);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return;
        try {
            String relativePath = fileUrl.replace("/uploads/", "");
            Path filePath = Paths.get(basePath, relativePath);
            Files.deleteIfExists(filePath);
            log.info("Deleted file: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to delete file: {}", fileUrl, e);
        }
    }

    @Override
    public String getFileUrl(String fileName) {
        return baseUrl + "/uploads/" + fileName;
    }
}
