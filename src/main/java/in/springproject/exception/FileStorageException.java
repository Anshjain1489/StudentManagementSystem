package in.springproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a file storage operation fails (upload, download, delete).
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FileStorageException extends RuntimeException {

    /**
     * Constructs a FileStorageException with the specified detail message.
     *
     * @param message the detail message
     */
    public FileStorageException(String message) {
        super(message);
    }

    /**
     * Constructs a FileStorageException with message and root cause.
     *
     * @param message the detail message
     * @param cause   the root cause
     */
    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
