package com.example.demoteste.excecao;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.demoteste.usuario.UsuarioNotFoundException;

/**
 * 
 * @author Thiago Gutenberg C. da Costa
 */
@ControllerAdvice
@RequestMapping(produces = "application/problem+json")
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    Problem handleUnknownException(Exception ex, WebRequest request) {
        return problem(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ UsuarioNotFoundException.class })
    ResponseEntity<Problem> handleNotFoundException(Exception ex, WebRequest request) {
        return problemEntity(ex, request, HttpStatus.NOT_FOUND);
    }

    protected Problem problem(Exception ex, WebRequest request, HttpStatus status) {
        String path = getRequestURI(request);
        String detail = Optional.ofNullable(ex.getCause())
                .map(Throwable::getMessage)
                .orElse(ExceptionUtils.getRootCauseMessage(ex));
        String title = Optional.ofNullable(ex.getMessage()).orElse(status.getReasonPhrase());
        return Problem.create()
                .withStatus(status)
                .withTitle(title)
                .withDetail(detail)
                .withInstance(URI.create(path))
                .withProperties(map -> {
                    map.put("timestamp", OffsetDateTime.now());
                    map.put("sessionId", getSessionId(request));
                });
    }

    protected ResponseEntity<Problem> problemEntity(Exception ex, WebRequest request, HttpStatus status) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem(ex, request, status));
    }

    protected String getSessionId(WebRequest request) {
        String sessionId = null;
        if (request instanceof ServletWebRequest) {
            HttpServletRequest httpRequest = parse(request);
            sessionId = httpRequest.getSession().getId();
        }
        return sessionId;
    }

    protected String getRequestURI(WebRequest request) {
        String path = null;
        if (request instanceof ServletWebRequest) {
            HttpServletRequest httpRequest = parse(request);
            path = httpRequest.getRequestURI();
        }
        return path;
    }

    private HttpServletRequest parse(WebRequest request) {
        return ((ServletWebRequest) request).getRequest();
    }
}
