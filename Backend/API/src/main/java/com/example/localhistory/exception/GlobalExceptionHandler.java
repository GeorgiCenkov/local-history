package com.example.localhistory.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/// Single place for all exception-to-HTTP-response mapping.
/// Keeps controllers and services free of try/catch boilerplate and guarantees
/// every error the client receives has the same JSON shape:
/// {
///   "timestamp": "...",
///   "status":    404,
///   "message":   "Landmark not found with id: 7"
/// }
/// For validation errors the body also contains an "errors" list.
/// IMPORTANT: Spring Security intercepts 401/403 at the filter chain level,
/// BEFORE requests reach this handler. To get consistent JSON for those too,
/// wire AuthEntryPoint and AccessDeniedHandlerImpl into your SecurityFilterChain:
///   http.exceptionHandling(ex -> ex
///       .authenticationEntryPoint(new AuthEntryPoint())
///       .accessDeniedHandler(new AccessDeniedHandlerImpl())
///   );
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 400 Bad Request ──────────────────────────────────────────────────────

    /**
     * Triggered when a @Valid-annotated request body fails bean validation.
     * Returns a list of "field: message" strings so the client knows exactly
     * which fields need fixing.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();

        Map<String, Object> body = buildBody(HttpStatus.BAD_REQUEST, "Validation failed");
        body.put("errors", errors);
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Handles IllegalArgumentException — thrown when business logic receives
     * a value that is syntactically fine but semantically wrong
     * (e.g. an unknown enum value after mapping).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return respond(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // ── 401 Unauthorized ─────────────────────────────────────────────────────

    /**
     * Catches Spring Security's AuthenticationException when it surfaces inside
     * a controller (e.g. via @PreAuthorize on a method that checks the token).
     * Filter-level 401s are caught by AuthEntryPoint instead.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthentication(AuthenticationException ex) {
        return respond(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    // ── 403 Forbidden ────────────────────────────────────────────────────────

    /**
     * Catches @PreAuthorize / @Secured failures that bubble up to this handler.
     * Filter-level 403s still need AccessDeniedHandlerImpl wired into the
     * SecurityFilterChain.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        return respond(HttpStatus.FORBIDDEN, "You do not have permission to perform this action");
    }

    // ── 404 Not Found ────────────────────────────────────────────────────────

    /** Thrown by services when a requested entity does not exist in the DB. */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(EntityNotFoundException ex) {
        return respond(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // ── 409 Conflict ─────────────────────────────────────────────────────────

    /**
     * Thrown when a resource already exists (e.g. duplicate email on register).
     * Services should throw ResourceConflictException for this case rather than
     * ResponseStatusException, keeping intent explicit.
     */
    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(ResourceConflictException ex) {
        return respond(HttpStatus.CONFLICT, ex.getMessage());
    }

    // ── ResponseStatusException ───────────────────────────────────────────────

    /**
     * ResponseStatusException lets any throw site attach an HTTP status without
     * a custom exception class. Handled here so the reason string is preserved
     * in the response body rather than swallowed.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        return respond(status, ex.getReason());
    }

    // ── 500 Catch-all ────────────────────────────────────────────────────────

    /**
     * Safety net for anything not matched above.
     * Always return a generic message to the client — never leak a stack trace.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        ex.printStackTrace(); // swap for SLF4J logger in production
        return respond(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private ResponseEntity<Map<String, Object>> respond(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(buildBody(status, message));
    }

    /** Builds the standard error body; callers may add extra keys (e.g. "errors"). */
    private Map<String, Object> buildBody(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("message", message);
        return body;
    }
}