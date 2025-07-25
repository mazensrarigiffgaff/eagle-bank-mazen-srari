package com.eaglebank.eagle_bank_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BankAccountNotFoundException extends ResponseStatusException {

    public BankAccountNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
