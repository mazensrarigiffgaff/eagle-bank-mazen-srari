package com.eaglebank.eagle_bank_api.controller;

import com.eaglebank.eagle_bank_api.service.BankAccountService;
import com.example.project.api.V1Api;
import com.example.project.model.BankAccountResponse;
import com.example.project.model.CreateBankAccountRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class BankAccountController implements V1Api {

    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping("/v1/accounts")
    @Override
    public ResponseEntity<BankAccountResponse> _createAccount(@Valid @RequestBody CreateBankAccountRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bankAccountService.createBankAccount(body));
    }

    @GetMapping("/v1/accounts/{accountNumber}")
    @Override
    public ResponseEntity<BankAccountResponse> _fetchAccountByAccountNumber(@PathVariable @Pattern(regexp = "^01\\d{6}$") String accountNumber) {
        BankAccountResponse response = bankAccountService.fetchByAccountNumber(accountNumber);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/v1/accounts/{accountNumber}")
    @Override
    public ResponseEntity<Void> _deleteAccountByAccountNumber(@PathVariable @Pattern(regexp = "^01\\d{6}$") String accountNumber) {
        bankAccountService.deleteBankAccount(accountNumber);
        return ResponseEntity.noContent().build();
    }

}
