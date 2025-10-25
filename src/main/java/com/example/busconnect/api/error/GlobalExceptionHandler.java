package com.example.busconnect.api.error;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<ApiError> build(
            HttpStatus status,
            String message,
            HttpServletRequest req
    ) {
        ApiError body = ApiError.of(
                status,
                message,
                req.getRequestURI(),
                null
        );
        return ResponseEntity.status(status).body(body);
    }

    // 401 - Credenciales inválidas
    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<ApiError> handleUnauthorized(
            Exception ex,
            HttpServletRequest req
    ) {
        logger.warn("Authentication failed: {}", ex.getMessage());
        
        String message = "Email o contraseña incorrectos";
        return build(HttpStatus.UNAUTHORIZED, message, req);
    }

    // 403 - Cuenta inactiva/bloqueada
    @ExceptionHandler({DisabledException.class})
    public ResponseEntity<ApiError> handleForbidden(
            Exception ex,
            HttpServletRequest req
    ) {
        logger.warn("Access forbidden: {}", ex.getMessage());
        String message = ex.getMessage();
        if (message == null || message.isBlank()) {
            message = "Tu cuenta no está activa. Contacta al administrador.";
        }
        return build(HttpStatus.FORBIDDEN, message, req);
    }

    // 404 - No encontrado
    @ExceptionHandler({EntityNotFoundException.class, NoSuchElementException.class})
    public ResponseEntity<ApiError> handleNotFound(
            RuntimeException ex,
            HttpServletRequest req
    ) {
        logger.warn("Recurso no encontrado: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    // 400 - Bad Request
    @ExceptionHandler({IllegalArgumentException.class, DateTimeParseException.class, IllegalStateException.class})
    public ResponseEntity<ApiError> handleBadRequest(
            Exception ex,
            HttpServletRequest req
    ) {
        logger.warn("Solicitud inválida: {}", ex.getMessage());
        String message = ex instanceof DateTimeParseException ?
                "Formato de fecha inválido." : ex.getMessage();
        return build(HttpStatus.BAD_REQUEST, message, req);
    }

    // 409 - Conflict (reglas de negocio, constraints BD)
    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<ApiError> handleConflict(
            RuntimeException ex,
            HttpServletRequest req
    ) {
        logger.warn("Conflicto de negocio: {}", ex.getMessage());
        String message = ex.getMessage();
        if (ex instanceof DataIntegrityViolationException) {
            if (message.contains("unique constraint") || message.contains("duplicate key")) {
                message = "El recurso ya existe o viola una restricción única.";
            }
        }
        return build(HttpStatus.CONFLICT, message, req);
    }

    // 422 - Validación fallida (Bean Validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest req
    ) {
        logger.warn("Error de validación en: {}", req.getRequestURI());
        
        List<ApiError.FieldViolation> violations = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> new ApiError.FieldViolation(
                        err.getField(),
                        err.getDefaultMessage()
                ))
                .toList();
                
        ApiError body = ApiError.of(
                HttpStatus.BAD_REQUEST,
                "Error de validación en los datos enviados",
                req.getRequestURI(),
                violations
        );
        return ResponseEntity.badRequest().body(body);
    }

    // Manejo de DuplicateResourceException
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicateResource(
            DuplicateResourceException ex,
            HttpServletRequest req
    ) {
        logger.warn("Recurso duplicado: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    // 500 - Error interno (catch-all)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOther(
            Exception ex,
            HttpServletRequest req
    ) {
        //  Manejar excepciones de seguridad
        if (ex instanceof org.springframework.security.access.AccessDeniedException ||
                ex instanceof org.springframework.security.authorization.AuthorizationDeniedException) {
            logger.warn("Access denied: {}", ex.getMessage());
            return build(HttpStatus.FORBIDDEN, "No tienes permisos para acceder a este recurso", req);
        }
        logger.error("Error interno no manejado en {}: {}", req.getRequestURI(), ex.getMessage(), ex);
        
        String msg = "Error interno del servidor. Por favor contacte al administrador.";
        return build(HttpStatus.INTERNAL_SERVER_ERROR, msg, req);
    }
}