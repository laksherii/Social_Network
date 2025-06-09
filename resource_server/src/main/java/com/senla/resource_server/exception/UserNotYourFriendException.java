package com.senla.resource_server.exception;

public class UserNotYourFriendException extends RuntimeException {
    public UserNotYourFriendException(String message) {
        super(message);
    }
}
