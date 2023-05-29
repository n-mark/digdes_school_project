package ru.digdes.school.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import ru.digdes.school.dto.exception.ExceptionDto;

import java.util.Date;

@ControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> globalExceptionHandler(Exception e, NativeWebRequest request) {
        HttpServletRequest httpServletRequest = request.getNativeRequest(HttpServletRequest.class);
        String httpMethod = httpServletRequest.getMethod();
        logger.error(httpMethod + " | " + request.getDescription(false) + " : " + e.getMessage());
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(e.getMessage())
                .timestamp(new Date())
                .description(httpMethod + " " + request.getDescription(false))
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionDto);
    }

    @ExceptionHandler({EntityNotFoundException.class, IllegalArgumentException.class})
    public ResponseEntity<ExceptionDto> badRequestHandler(Exception e, NativeWebRequest request) {
        HttpServletRequest httpServletRequest = request.getNativeRequest(HttpServletRequest.class);
        String httpMethod = httpServletRequest.getMethod();
        logger.error(httpMethod + " | " + request.getDescription(false) + " : " + e.getMessage());
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .timestamp(new Date())
                .description(httpMethod + " " + request.getDescription(false))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionDto);
    }
}
