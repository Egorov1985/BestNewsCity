package com.egorov.bestnewscity.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.List;

@ControllerAdvice
public class AppException {

    @ExceptionHandler (WebExchangeBindException.class)
    public ResponseEntity<List<String>> handleException(WebExchangeBindException e){
        List<String> errors = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler({NotFoundNewsException.class})
    public ResponseEntity<?> handleNotFoundNews(NotFoundNewsException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
