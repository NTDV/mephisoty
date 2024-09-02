package ru.valkovets.mephisoty.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.valkovets.mephisoty.api.dto.ErrorDto;

@ControllerAdvice
public class DefaultHandler {
@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorDto> handleException(final Exception e) {
    return new ResponseEntity<>(ErrorDto.from(e), HttpStatus.OK);
}
}
