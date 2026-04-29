package com.api.commons;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the Real Estate API.
 *
 * <p>Intercepts exceptions thrown by any {@code @RestController} and converts
 * them into structured JSON error responses, ensuring consistent error payloads
 * across the entire API surface.</p>
 *
 * <p>Handled exception types:</p>
 * <ul>
 *   <li>{@link MethodArgumentNotValidException} — Bean Validation failures (HTTP 400)</li>
 *   <li>{@link ResourceNotFoundException} — missing entities (HTTP 404)</li>
 *   <li>{@link Exception} — all other unexpected errors (HTTP 500)</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles Bean Validation errors raised when a request body or parameter
     * fails {@code @Valid} / {@code @Validated} constraints.
     *
     * @param ex the exception containing field-level binding errors
     * @return a {@code 400 Bad Request} response whose body maps each invalid
     *         field name to its corresponding constraint violation message
     */
    // Validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Handles {@link ResourceNotFoundException} thrown when a requested entity
     * does not exist in the database.
     *
     * @param ex the exception carrying the not-found detail message
     * @return a {@code 404 Not Found} response with an {@code "error"} field
     *         containing the exception message
     */
    // Resource not found errors
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(ResourceNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Catch-all handler for any {@link Exception} not matched by a more specific
     * handler above.
     *
     * @param ex the unhandled exception
     * @return a {@code 500 Internal Server Error} response with an {@code "error"}
     *         field prefixed with {@code "Unexpected error: "}
     */
    // Catch-all fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Unexpected error: " + ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
