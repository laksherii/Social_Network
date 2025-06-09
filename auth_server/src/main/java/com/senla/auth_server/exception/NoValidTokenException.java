package com.senla.auth_server.exception;

public class NoValidTokenException extends RuntimeException {
    public NoValidTokenException(String message) {
        super(message);
    }
}
