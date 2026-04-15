package com.project.tesi.exception;

import com.project.tesi.exception.common.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestore globale delle eccezioni REST.
 * Intercetta tutte le eccezioni lanciate dai controller e restituisce
 * risposte JSON uniformi tramite {@link ErrorResponse}.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Gestione eccezioni custom
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex, HttpServletRequest request) {
        HttpStatus status = ex.getStatus();
        if (status.is5xxServerError()) {
            log.error("Errore interno: {} — Path: {}", ex.getMessage(), request.getRequestURI(), ex);
        } else {
            log.warn("Eccezione business [{}]: {} — Path: {}", status.value(), ex.getMessage(), request.getRequestURI());
        }
        return buildErrorResponse(ex.getMessage(), status, request);
    }

    // Gestione login fallito
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(HttpServletRequest request) {
        log.warn("Tentativo di login fallito — Path: {}", request.getRequestURI());
        return buildErrorResponse("Email o password non validi", HttpStatus.UNAUTHORIZED, request);
    }

    // Gestione accesso negato
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(HttpServletRequest request) {
        log.warn("Accesso negato — Path: {}", request.getRequestURI());
        return buildErrorResponse("Non hai i permessi per accedere a questa risorsa", HttpStatus.FORBIDDEN, request);
    }

    // Gestione errori di validazione
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Errore di validazione")
                .path(request.getRequestURI())
                .validationErrors(errors)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Gestione argomento non valido
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Argomento non valido: {} — Path: {}", ex.getMessage(), request.getRequestURI());
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    // Gestione stato non valido
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex, HttpServletRequest request) {
        log.warn("Stato non valido: {} — Path: {}", ex.getMessage(), request.getRequestURI());
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request);
    }

    // Gestione limite dimensione
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSize(HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(413);
        return buildErrorResponse("File troppo grande", status, request);
    }

    // Gestione risorsa non trovata
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(HttpServletRequest request) {
        return buildErrorResponse("Endpoint non trovato: " + request.getRequestURI(), HttpStatus.NOT_FOUND, request);
    }

    // Fallback globale
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, HttpServletRequest request) {
        // TODO: valutare l'aggiunta del log dello stack trace a fini di debug
        log.error("Errore imprevisto — Path: {} — Tipo: {}", request.getRequestURI(), ex.getClass().getSimpleName(), ex);
        return buildErrorResponse(
                "Errore interno",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
    }

    // Utility di risposta
    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, status);
    }
}