package com.egorov.bestnewscity.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.List;

@RestControllerAdvice
public class AppException  {

    @ExceptionHandler ({WebExchangeBindException.class})
    public Mono<ResponseEntity<List<String>>> handleException(WebExchangeBindException e){
        List<String> errors = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        return Mono.fromSupplier(() -> ResponseEntity.badRequest().body(errors));
    }

    @ExceptionHandler({NotFoundNewsException.class})
    public Mono<ResponseEntity<String>> handleNotFoundNews(NotFoundNewsException e)   {
        return Mono.just(new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND));
    }
}
