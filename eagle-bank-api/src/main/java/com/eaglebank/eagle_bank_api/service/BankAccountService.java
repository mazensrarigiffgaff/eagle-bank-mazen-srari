package com.eaglebank.eagle_bank_api.service;

import com.eaglebank.eagle_bank_api.exception.BadBankAccountRequestException;
import com.eaglebank.eagle_bank_api.exception.BankAccountNotFoundException;
import com.eaglebank.eagle_bank_api.model.BankAccountEntity;
import com.eaglebank.eagle_bank_api.repository.BankAccountRepository;
import com.example.project.model.BankAccountResponse;
import com.example.project.model.CreateBankAccountRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;

    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    public BankAccountResponse createBankAccount(CreateBankAccountRequest createBankAccountRequest) {
        if (createBankAccountRequest == null) {
            throw new BadBankAccountRequestException("Bad request: create bank account request must be valid");
        }
        validateCreateBankAccountRequest(createBankAccountRequest);

        BankAccountEntity bankAccount = convertToEntity(createBankAccountRequest);
        bankAccount.setAccountNumber(generateAccountNumber());

        BankAccountEntity savedAccount = bankAccountRepository.save(bankAccount);
        return convertToResponse(savedAccount);
    }

    public BankAccountResponse fetchByAccountNumber(String accountNumber) {
        validateAccountNumber(accountNumber);

        BankAccountEntity account = bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found with account number: " + accountNumber));

        return convertToResponse(account);
    }

    public void deleteBankAccount(String accountNumber) {
        validateAccountNumber(accountNumber);

        BankAccountEntity account = bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found while attempting deletion. Account number: " + accountNumber));

        bankAccountRepository.delete(account);
    }

    private void validateCreateBankAccountRequest(CreateBankAccountRequest request) {
        List<String> errors = new ArrayList<>();

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            errors.add("Name is required and cannot be empty");
        } else if (request.getName().trim().length() > 100) {
            errors.add("Name cannot exceed 100 characters");
        }

        if (request.getAccountType() == null) {
            errors.add("Account type is required");
        } else {
            validateAccountType(request.getAccountType().getValue(), errors);
        }

        if (!errors.isEmpty()) {
            throw new BadBankAccountRequestException("Validation failed: " + String.join(", ", errors));
        }
    }

    private void validateAccountType(String accountType, List<String> errors) {
        if (accountType == null || accountType.trim().isEmpty()) {
            errors.add("Account type cannot be empty");
        } else if (!"personal".equals(accountType.trim().toLowerCase())) {
            errors.add("Account type must be 'personal'");
        }
    }

    private void validateAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number is required");
        }

        if (!accountNumber.matches("^01\\d{6}$")) {
            throw new IllegalArgumentException("Invalid account number format. Expected format: 01XXXXXX (8 digits starting with 01)");
        }
    }

    private BankAccountEntity convertToEntity(CreateBankAccountRequest createRequest) {
        return BankAccountEntity.builder()
                .name(createRequest.getName())
                .accountType(BankAccountResponse.AccountTypeEnum.fromValue(createRequest.getAccountType().getValue()))
                .sortCode(BankAccountResponse.SortCodeEnum._10_10_10)
                .balance(0.0)
                .currency(BankAccountResponse.CurrencyEnum.GBP)
                .build();
    }

    private BankAccountResponse convertToResponse(BankAccountEntity entity) {
        BankAccountResponse response = new BankAccountResponse();
        response.setAccountNumber(entity.getAccountNumber());
        response.setSortCode(BankAccountResponse.SortCodeEnum.fromValue(entity.getSortCode().toString()));
        response.setName(entity.getName());
        response.setAccountType(BankAccountResponse.AccountTypeEnum.fromValue(entity.getAccountType().getValue()));
        response.setBalance(entity.getBalance());
        response.setCurrency(BankAccountResponse.CurrencyEnum.fromValue(entity.getCurrency().toString()));
        response.setCreatedTimestamp(entity.getCreatedTimestamp());
        response.setUpdatedTimestamp(entity.getUpdatedTimestamp());
        return response;
    }

    private String generateAccountNumber() {

        Random random = new Random();
        int randomNumber = 100000 + random.nextInt(900000);

        String accountNumber = "01" + randomNumber;

        while (bankAccountRepository.findByAccountNumber(accountNumber).isPresent()) {
            randomNumber = 100000 + random.nextInt(900000);
            accountNumber = "01" + randomNumber;
        }

        return accountNumber;
    }
}