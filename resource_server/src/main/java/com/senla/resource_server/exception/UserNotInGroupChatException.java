package com.senla.resource_server.exception;

public class UserNotInGroupChatException extends RuntimeException {
    public UserNotInGroupChatException(String message) {
        super(message);
    }
}
