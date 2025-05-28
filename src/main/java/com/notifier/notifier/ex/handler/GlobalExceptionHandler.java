package com.notifier.notifier.ex.handler;

import com.notifier.notifier.dto.ErrorDto;
import com.notifier.notifier.ex.NotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)

    public ResponseEntity<ErrorDto> handleNotFoundException(Exception ex) {

        log.error("not found ex: {}", ex.getMessage());
        return new ResponseEntity<>(ErrorDto.builder()
                .message(ex.getMessage())
                .statusCode(HttpStatus.NOT_FOUND.value())
                .build(), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            ConstraintViolationException.class, MethodArgumentNotValidException.class, IllegalArgumentException.class,
            HttpMessageNotReadableException.class, DataIntegrityViolationException.class, MissingServletRequestParameterException.class
    })
    public ResponseEntity<ErrorDto> handleWrongArgsException(Exception ex) {

        log.error("wrong args: {}", ex.getMessage());
        return new ResponseEntity<>(ErrorDto.builder()
                .message("bad request")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDto> handleOtherException(Exception ex) {

        log.error("some problem: {}", ex.toString());
        return new ResponseEntity<>(ErrorDto.builder()
                .message(ex.getMessage())
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
