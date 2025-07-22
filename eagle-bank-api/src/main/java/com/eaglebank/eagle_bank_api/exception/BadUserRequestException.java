package com.eaglebank.eagle_bank_api.exception;

public class BadUserRequestException extends RuntimeException {

    public BadUserRequestException(String message) {
        super(message);
    }
}
