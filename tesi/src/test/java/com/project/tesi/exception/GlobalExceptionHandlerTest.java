package com.project.tesi.exception;

import com.project.tesi.exception.common.BaseException;
import com.project.tesi.exception.common.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.HttpMethod;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test unitari per {@link GlobalExceptionHandler}.
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    @DisplayName("handleBaseException — eccezione custom 404")
    void handleBaseException_404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Utente", 1L);
        ResponseEntity<ErrorResponse> resp = handler.handleBaseException(ex, request);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resp.getBody().getMessage()).contains("1");
        assertThat(resp.getBody().getPath()).isEqualTo("/api/test");
    }

    @Test
    @DisplayName("handleBaseException — eccezione custom 500 (server error)")
    void handleBaseException_500() {
        BaseException ex = new BaseException("Errore interno", HttpStatus.INTERNAL_SERVER_ERROR) {};
        ResponseEntity<ErrorResponse> resp = handler.handleBaseException(ex, request);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("handleBadCredentials — 401")
    void handleBadCredentials() {
        ResponseEntity<ErrorResponse> resp = handler.handleBadCredentials(
                new BadCredentialsException("bad"), request);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(resp.getBody().getMessage()).contains("password");
    }

    @Test
    @DisplayName("handleAccessDenied — 403")
    void handleAccessDenied() {
        ResponseEntity<ErrorResponse> resp = handler.handleAccessDenied(
                new AccessDeniedException("denied"), request);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("handleIllegalArgument — 400")
    void handleIllegalArgument() {
        ResponseEntity<ErrorResponse> resp = handler.handleIllegalArgument(
                new IllegalArgumentException("campo non valido"), request);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody().getMessage()).contains("campo non valido");
    }

    @Test
    @DisplayName("handleIllegalState — 409")
    void handleIllegalState() {
        ResponseEntity<ErrorResponse> resp = handler.handleIllegalState(
                new IllegalStateException("stato non valido"), request);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("handleMaxUploadSize — 413")
    void handleMaxUploadSize() {
        ResponseEntity<ErrorResponse> resp = handler.handleMaxUploadSize(
                new MaxUploadSizeExceededException(1024L), request);
        assertThat(resp.getStatusCode().value()).isEqualTo(413);
    }

    @Test
    @DisplayName("handleGlobalException — 500 fallback")
    void handleGlobalException() {
        ResponseEntity<ErrorResponse> resp = handler.handleGlobalException(
                new RuntimeException("errore imprevisto"), request);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(resp.getBody().getMessage()).contains("errore interno");
    }

    @Test
    @DisplayName("handleMissingParams — 400")
    void handleMissingParams() {
        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("userId", "Long");
        ResponseEntity<ErrorResponse> resp = handler.handleMissingParams(ex, request);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody().getMessage()).contains("userId");
    }

    @Test
    @DisplayName("handleTypeMismatch — 400")
    void handleTypeMismatch() {
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "abc", Long.class, "userId", null, new RuntimeException());
        ResponseEntity<ErrorResponse> resp = handler.handleTypeMismatch(ex, request);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody().getMessage()).contains("userId");
    }

    @Test
    @DisplayName("handleTypeMismatch — requiredType null")
    void handleTypeMismatch_nullType() {
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "abc", null, "param", null, new RuntimeException());
        ResponseEntity<ErrorResponse> resp = handler.handleTypeMismatch(ex, request);
        assertThat(resp.getBody().getMessage()).contains("sconosciuto");
    }

    @Test
    @DisplayName("handleValidationExceptions — 400 con mappa degli errori per campo")
    void handleValidationExceptions() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("loginRequest", "email", "non deve essere vuoto");
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErrorResponse> resp = handler.handleValidationExceptions(ex, request);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody().getMessage()).contains("validazione");
        assertThat(resp.getBody().getValidationErrors()).containsKey("email");
        assertThat(resp.getBody().getValidationErrors().get("email")).isEqualTo("non deve essere vuoto");
    }

    @Test
    @DisplayName("handleValidationExceptions — più campi errati")
    void handleValidationExceptions_multipleErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError err1 = new FieldError("req", "email", "obbligatorio");
        FieldError err2 = new FieldError("req", "password", "min 6 caratteri");
        when(bindingResult.getAllErrors()).thenReturn(List.of(err1, err2));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErrorResponse> resp = handler.handleValidationExceptions(ex, request);

        assertThat(resp.getBody().getValidationErrors()).hasSize(2);
    }

    @Test
    @DisplayName("handleNoResourceFound — 404")
    void handleNoResourceFound() throws Exception {
        NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.GET, "/api/nonexistent", "Risorsa non trovata");
        ResponseEntity<ErrorResponse> resp = handler.handleNoResourceFound(ex, request);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resp.getBody().getMessage()).contains("Endpoint non trovato");
    }
}




