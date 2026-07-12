package in.springproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a JWT token has expired.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TokenExpiredException extends RuntimeException {

    /**
     * Constructs a TokenExpiredException with the specified detail message.
     *
     * @param message the detail message
     */
    public TokenExpiredException(String message) {
        super(message);
    }
}
