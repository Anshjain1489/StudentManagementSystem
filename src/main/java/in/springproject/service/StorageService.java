ackage in.springproject.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Abstraction over the file storage backend.
 * Default implementation uses the local file system; swap for S3/GCS in production.
 */
public interface StorageService {

    /**
     * Store a file under the given subdirectory and return its public URL.
     *
     * @param file         the multipart file to store
     * @param subdirectory relative subdirectory inside the storage root (e.g. "teachers/photos")
     * @return the accessible URL / path for the stored file
     */
    String storeFile(MultipartFile file, String subdirectory);

    /**
     * Delete a file by its URL.
     *
     * @param fileUrl the URL previously returned by {@link #storeFile}
     */
    void deleteFile(String fileUrl);

    /**
     * Build the public URL for a file name.
     *
     * @param fileName the file name (relative to the storage root)
     * @return full public URL
     */
    String getFileUrl(String fileName);
}
