package ru.valkovets.mephisoty.application.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.valkovets.mephisoty.api.dto.ErrorDto;

@Slf4j
@ControllerAdvice
public class DefaultHandler {
@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorDto> handleException(final Exception e) {
    log.error("Exception caught", e);
    return new ResponseEntity<>(ErrorDto.from(e), HttpStatus.OK);
}
}
