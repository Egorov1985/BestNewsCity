package com.egorov.bestnewscity.exception;

public class NotFoundNewsException extends RuntimeException{
    public NotFoundNewsException(String message) {
        super(message);
    }
}
