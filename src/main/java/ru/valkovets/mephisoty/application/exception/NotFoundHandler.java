package ru.valkovets.mephisoty.application.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class NotFoundHandler {
private static final Logger log = LoggerFactory.getLogger(NotFoundHandler.class);
@Value("${spring.web.resources.static-locations}")
private String frontDir = "file:/home/divalkovets/front";
private final FileSystemResource index = new FileSystemResource(Path.of(frontDir.substring(5), "/index.html"));

@ExceptionHandler({ NoHandlerFoundException.class, NoResourceFoundException.class })
public ResponseEntity<String> renderDefaultPage() {
  try {

    final String body = StreamUtils.copyToString(index.getInputStream(), Charset.defaultCharset());
    return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(body);

  } catch (final IOException e) {
    log.error("Failed to load index.html", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("There was an error completing the action.");
  }
}
}
