package com.eaglebank.eagle_bank_api.exception;

public class BadBankAccountRequestException extends RuntimeException {

    public BadBankAccountRequestException(String message) {
        super(message);
    }
}
