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
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestore globale delle eccezioni REST.
 * Intercetta tutte le eccezioni e restituisce risposte JSON uniformi.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ══════════════════════════════════════════════════════════════════════
    //  1. ECCEZIONI CUSTOM (BaseException e figlie)
    //     Gestisce automaticamente TUTTE le eccezioni che estendono BaseException:
    //     - ResourceNotFoundException       → 404
    //     - ResourceAlreadyExistsException   → 409
    //     - UnauthorizedAccessException      → 403
    //     - BusinessLogicException           → 422
    //     - SlotAlreadyBookedException       → 409
    //     - InsufficientCreditsException     → 422
    //     - NoActiveSubscriptionException    → 422
    //     - ProfessionalNotAssignedException → 403
    //     - ProfessionalSoldOutException     → 422
    //     - ChatNotAllowedException          → 403
    //     - DocumentNotFoundException        → 404
    //     - DocumentStorageException         → 500
    //     - InvalidFileException             → 400
    //     - InvalidCredentialsException      → 401
    //     - SubscriptionNotFoundException    → 404
    //     - ReviewNotAllowedException        → 422
    // ══════════════════════════════════════════════════════════════════════
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

    // ══════════════════════════════════════════════════════════════════════
    //  2. AUTENTICAZIONE — Login fallito (Spring Security)
    // ══════════════════════════════════════════════════════════════════════
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        log.warn("Tentativo di login fallito — Path: {}", request.getRequestURI());
        return buildErrorResponse("Email o password non validi.", HttpStatus.UNAUTHORIZED, request);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  3. AUTORIZZAZIONE — Accesso negato (Spring Security @PreAuthorize)
    // ══════════════════════════════════════════════════════════════════════
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Accesso negato — Path: {}", request.getRequestURI());
        return buildErrorResponse("Non hai i permessi per accedere a questa risorsa.", HttpStatus.FORBIDDEN, request);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  4. VALIDAZIONE — Errori @Valid nei DTO (@NotBlank, @Email, ecc.)
    // ══════════════════════════════════════════════════════════════════════
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
                .message("Errore di validazione dei dati inviati.")
                .path(request.getRequestURI())
                .validationErrors(errors)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  5. PARAMETRI MANCANTI O ERRATI
    // ══════════════════════════════════════════════════════════════════════
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException ex, HttpServletRequest request) {
        String message = "Parametro obbligatorio mancante: '" + ex.getParameterName() + "'.";
        return buildErrorResponse(message, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String message = "Valore non valido per il parametro '" + ex.getName() + "'. Tipo atteso: "
                + (ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "sconosciuto") + ".";
        return buildErrorResponse(message, HttpStatus.BAD_REQUEST, request);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  6. ARGOMENTI ILLEGALI / STATO ILLEGALE
    // ══════════════════════════════════════════════════════════════════════
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Argomento non valido: {} — Path: {}", ex.getMessage(), request.getRequestURI());
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex, HttpServletRequest request) {
        log.warn("Stato non valido: {} — Path: {}", ex.getMessage(), request.getRequestURI());
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  7. FILE UPLOAD — Dimensione massima superata
    // ══════════════════════════════════════════════════════════════════════
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSize(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(413);
        return buildErrorResponse("Il file caricato supera la dimensione massima consentita.", status, request);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  8. RISORSA STATICA NON TROVATA (Spring 6+)
    // ══════════════════════════════════════════════════════════════════════
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request) {
        return buildErrorResponse("Endpoint non trovato: " + request.getRequestURI(), HttpStatus.NOT_FOUND, request);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  9. FALLBACK GLOBALE — Tutti gli altri errori non previsti
    //     Non espone mai la stack trace al client.
    // ══════════════════════════════════════════════════════════════════════
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, HttpServletRequest request) {
        log.error("Errore imprevisto — Path: {} — Tipo: {}", request.getRequestURI(), ex.getClass().getSimpleName(), ex);
        return buildErrorResponse(
                "Si è verificato un errore interno. Riprova più tardi o contatta l'assistenza.",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
    }

    // ══════════════════════════════════════════════════════════════════════
    //  UTILITY — Costruzione risposta di errore uniforme
    // ══════════════════════════════════════════════════════════════════════
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