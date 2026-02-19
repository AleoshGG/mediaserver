package aleosh.online.mediaserver.core.controllers;

import aleosh.online.mediaserver.core.dtos.BaseResponse;
import aleosh.online.mediaserver.core.dtos.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    private boolean isDevelopment() {
        return "dev".equals(activeProfile) || "development".equals(activeProfile);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<ErrorResponse>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        List<String> details = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            details.add(fieldName + ": " + errorMessage);
        });

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Error de validación en los datos de entrada",
                "VALIDATION_ERROR",
                details
        );

        BaseResponse<ErrorResponse> response = new BaseResponse<>(
                false,
                errorResponse,
                "Error de validación",
                HttpStatus.BAD_REQUEST
        );

        logger.warn("Validation error: {}", details);
        return response.buildResponseEntity();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<ErrorResponse>> handleIllegalArgumentException(
            IllegalArgumentException ex) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                "INVALID_ARGUMENT",
                null
        );

        BaseResponse<ErrorResponse> response = new BaseResponse<>(
                false,
                errorResponse,
                "Argumento inválido",
                HttpStatus.BAD_REQUEST
        );

        logger.warn("Illegal argument: {}", ex.getMessage());
        return response.buildResponseEntity();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseResponse<ErrorResponse>> handleRuntimeException(
            RuntimeException ex) {

        String errorMessage = isDevelopment() ? ex.getMessage() : "Error interno del servidor";
        List<String> details = isDevelopment() ? List.of(ex.getClass().getSimpleName()) : null;

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                errorMessage,
                "RUNTIME_ERROR",
                details
        );

        BaseResponse<ErrorResponse> response = new BaseResponse<>(
                false,
                errorResponse,
                "Error en tiempo de ejecución",
                HttpStatus.INTERNAL_SERVER_ERROR
        );

        logger.error("Runtime error: {}", ex.getMessage(), ex);
        return response.buildResponseEntity();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<BaseResponse<ErrorResponse>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex) {

        String errorMessage = "Violación de integridad de datos";
        if (isDevelopment()) {
            errorMessage = ex.getMostSpecificCause() != null ?
                    ex.getMostSpecificCause().getMessage() : ex.getMessage();
        }

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                errorMessage,
                "DATA_INTEGRITY_VIOLATION",
                isDevelopment() ? List.of(ex.getClass().getSimpleName()) : null
        );

        BaseResponse<ErrorResponse> response = new BaseResponse<>(
                false,
                errorResponse,
                "Error de integridad de datos",
                HttpStatus.CONFLICT
        );

        logger.error("Data integrity violation: {}", ex.getMessage(), ex);
        return response.buildResponseEntity();
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<BaseResponse<ErrorResponse>> handleDataAccessException(
            DataAccessException ex) {

        String errorMessage = "Error de acceso a datos";
        if (isDevelopment()) {
            errorMessage = ex.getMessage();
        }

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                errorMessage,
                "DATA_ACCESS_ERROR",
                isDevelopment() ? List.of(ex.getClass().getSimpleName()) : null
        );

        BaseResponse<ErrorResponse> response = new BaseResponse<>(
                false,
                errorResponse,
                "Error de acceso a datos",
                HttpStatus.INTERNAL_SERVER_ERROR
        );

        logger.error("Data access error: {}", ex.getMessage(), ex);
        return response.buildResponseEntity();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponse<ErrorResponse>> handleConstraintViolationException(
            ConstraintViolationException ex) {

        List<String> details = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Violación de restricciones de validación",
                "CONSTRAINT_VIOLATION",
                details
        );

        BaseResponse<ErrorResponse> response = new BaseResponse<>(
                false,
                errorResponse,
                "Error de validación de restricciones",
                HttpStatus.BAD_REQUEST
        );

        logger.warn("Constraint violation: {}", details);
        return response.buildResponseEntity();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse<ErrorResponse>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {

        String errorMessage = "Formato de mensaje inválido";
        if (isDevelopment()) {
            errorMessage = ex.getMessage();
        }

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                errorMessage,
                "INVALID_MESSAGE_FORMAT",
                isDevelopment() ? List.of(ex.getClass().getSimpleName()) : null
        );

        BaseResponse<ErrorResponse> response = new BaseResponse<>(
                false,
                errorResponse,
                "Formato de mensaje inválido",
                HttpStatus.BAD_REQUEST
        );

        logger.warn("Invalid message format: {}", ex.getMessage());
        return response.buildResponseEntity();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseResponse<ErrorResponse>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {

        Class<?> requiredType = ex.getRequiredType();
        String typeName = requiredType != null ? requiredType.getSimpleName() : "desconocido";
        String errorMessage = String.format("El parámetro '%s' debe ser de tipo %s", ex.getName(), typeName);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                errorMessage,
                "TYPE_MISMATCH",
                isDevelopment() ? List.of(ex.getClass().getSimpleName()) : null
        );

        BaseResponse<ErrorResponse> response = new BaseResponse<>(
                false,
                errorResponse,
                "Tipo de parámetro incorrecto",
                HttpStatus.BAD_REQUEST
        );

        logger.warn("Type mismatch for parameter '{}': {}", ex.getName(), ex.getMessage());
        return response.buildResponseEntity();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<BaseResponse<ErrorResponse>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex) {

        String errorMessage = String.format("Parámetro requerido '%s' de tipo %s no encontrado",
                ex.getParameterName(), ex.getParameterType());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                errorMessage,
                "MISSING_PARAMETER",
                null
        );

        BaseResponse<ErrorResponse> response = new BaseResponse<>(
                false,
                errorResponse,
                "Parámetro requerido faltante",
                HttpStatus.BAD_REQUEST
        );

        logger.warn("Missing required parameter: {}", ex.getParameterName());
        return response.buildResponseEntity();
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BaseResponse<ErrorResponse>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex) {

        String errorMessage = String.format("Método %s no soportado. Métodos soportados: %s",
                ex.getMethod(), String.join(", ", ex.getSupportedMethods()));

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                errorMessage,
                "METHOD_NOT_SUPPORTED",
                null
        );

        BaseResponse<ErrorResponse> response = new BaseResponse<>(
                false,
                errorResponse,
                "Método HTTP no soportado",
                HttpStatus.METHOD_NOT_ALLOWED
        );

        logger.warn("Unsupported HTTP method: {}", ex.getMethod());
        return response.buildResponseEntity();
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<BaseResponse<ErrorResponse>> handleNoHandlerFoundException(
            NoHandlerFoundException ex) {

        String errorMessage = String.format("No se encontró handler para %s %s",
                ex.getHttpMethod(), ex.getRequestURL());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                errorMessage,
                "ENDPOINT_NOT_FOUND",
                null
        );

        BaseResponse<ErrorResponse> response = new BaseResponse<>(
                false,
                errorResponse,
                "Endpoint no encontrado",
                HttpStatus.NOT_FOUND
        );

        logger.warn("No handler found for {} {}", ex.getHttpMethod(), ex.getRequestURL());
        return response.buildResponseEntity();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<ErrorResponse>> handleGenericException(
            Exception ex) {

        String errorMessage = isDevelopment() ? ex.getMessage() : "Error inesperado del servidor";
        List<String> details = isDevelopment() ? List.of(ex.getClass().getSimpleName()) : null;

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                errorMessage,
                "UNEXPECTED_ERROR",
                details
        );

        BaseResponse<ErrorResponse> response = new BaseResponse<>(
                false,
                errorResponse,
                "Error inesperado del servidor",
                HttpStatus.INTERNAL_SERVER_ERROR
        );

        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        return response.buildResponseEntity();
    }


}
