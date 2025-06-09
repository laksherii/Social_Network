package com.senla.resource_server.exception;

public class UserNotAdminInGroupException extends RuntimeException {
    public UserNotAdminInGroupException(String message) {
        super(message);
    }
}
