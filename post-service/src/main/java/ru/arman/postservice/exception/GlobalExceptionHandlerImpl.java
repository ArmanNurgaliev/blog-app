package ru.arman.postservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandlerImpl {
    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleBadRequestException(RuntimeException e, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(errorResponseBuilder(e, request));
    }

    @ExceptionHandler({PostNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException e, HttpServletRequest request) {
        return new ResponseEntity<>(errorResponseBuilder(e, request), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();

        StringBuilder errorMessage = new StringBuilder();

        for(ObjectError error : allErrors) {
            errorMessage.append(error.getDefaultMessage());
            if (allErrors.indexOf(error) != allErrors.size()-1)
                errorMessage.append("; ");
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .detail(errorMessage.toString())
                .request(request.getMethod() + " " + request.getRequestURI())
                .time(getTime())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    private ErrorResponse errorResponseBuilder(Exception exception, HttpServletRequest request) {
        log.info(getTime() + "\n" + exception.getMessage());
        log.info(request.getMethod() + " " + request.getRequestURI());

        return ErrorResponse.builder()
                .detail(exception.getMessage())
                .request(request.getMethod() + " " + request.getRequestURI())
                .time(getTime())
                .build();
    }

    private String getTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    @Builder
    @Value
    public static class ErrorResponse {
        String detail;
        String request;
        String time;
    }
}
