package in.springproject.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard API response wrapper used by all REST endpoints.
 * Ensures consistent JSON structure across the entire API.
 *
 * @param <T> the type of the response data payload
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /** Indicates whether the operation was successful. */
    private boolean success;

    /** Human-readable message describing the result. */
    private String message;

    /** The response payload; may be null for error responses. */
    private T data;

    /** HTTP status code mirrored in the body for client convenience. */
    private int statusCode;

    /** Server-side timestamp of when the response was generated. */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Creates a successful response with HTTP 200.
     *
     * @param message descriptive success message
     * @param data    the response payload
     * @param <T>     type of the data
     * @return ApiResponse with success=true and statusCode=200
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .statusCode(200)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a successful response with a custom HTTP status code.
     *
     * @param message    descriptive success message
     * @param data       the response payload
     * @param statusCode the HTTP status code (e.g., 201 for Created)
     * @param <T>        type of the data
     * @return ApiResponse with success=true and the given statusCode
     */
    public static <T> ApiResponse<T> success(String message, T data, int statusCode) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .statusCode(statusCode)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates an error response with no data payload.
     *
     * @param message    error message describing what went wrong
     * @param statusCode the HTTP status code (e.g., 404, 500)
     * @param <T>        type parameter (unused; data will be null)
     * @return ApiResponse with success=false and no data
     */
    public static <T> ApiResponse<T> error(String message, int statusCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .statusCode(statusCode)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
