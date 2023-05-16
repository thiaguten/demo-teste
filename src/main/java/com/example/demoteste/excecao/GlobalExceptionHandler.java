package com.example.demoteste.excecao;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.demoteste.usuario.UsuarioNotFoundException;

/**
 * 
 * @author Thiago Gutenberg C. da Costa
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    ResponseEntity<Problem> handleUnknownException(Exception ex, HttpServletRequest request) {
        return problemEntity(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ UsuarioNotFoundException.class })
    ResponseEntity<Problem> handleNotFoundException(Exception ex, HttpServletRequest request) {
        return problemEntity(ex, request, HttpStatus.NOT_FOUND);
    }

    protected Problem problem(Exception ex, HttpServletRequest request, HttpStatus status) {
        String path = request.getRequestURI();
        String detail = Optional.ofNullable(ex.getCause())
                .map(Throwable::getMessage)
                .orElse(ExceptionUtils.getRootCauseMessage(ex));
        String title = Optional.ofNullable(ex.getMessage()).orElse(status.getReasonPhrase());
        return Problem.create()
                .withStatus(status)
                .withTitle(title)
                .withDetail(detail)
                .withInstance(URI.create(path))
                .withProperties(map -> map.put("timestamp", OffsetDateTime.now()));
    }

    protected ResponseEntity<Problem> problemEntity(Exception ex, HttpServletRequest request, HttpStatus status) {
        log.error("", ex);
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem(ex, request, status));
    }
}
