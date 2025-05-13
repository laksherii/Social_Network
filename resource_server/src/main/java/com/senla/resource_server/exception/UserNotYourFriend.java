package com.senla.resource_server.exception;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserNotYourFriend extends RuntimeException {
    public UserNotYourFriend(String message) {
        super(message);
    }
}
