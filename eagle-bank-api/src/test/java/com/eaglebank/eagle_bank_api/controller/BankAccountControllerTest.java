package com.eaglebank.eagle_bank_api.controller;

import com.eaglebank.eagle_bank_api.service.BankAccountService;
import com.example.project.model.BankAccountResponse;
import com.example.project.model.CreateBankAccountRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountControllerTest {

    @Mock
    private BankAccountService bankAccountService;

    @InjectMocks
    private BankAccountController bankAccountController;

    private BankAccountResponse bankAccountResponse;
    private CreateBankAccountRequest createBankAccountRequest;

    @BeforeEach
    void setUp() {
        bankAccountResponse = createMockBankAccountResponse();
        createBankAccountRequest = new CreateBankAccountRequest();
        createBankAccountRequest.setName("My Personal Bank Account");
        createBankAccountRequest.setAccountType(CreateBankAccountRequest.AccountTypeEnum.PERSONAL);
    }

    @Nested
    class CreateBankAccountTests {

        @Test
        @DisplayName("Should create bank account successfully")
        void shouldCreateBankAccountSuccessfully() {
            when(bankAccountService.createBankAccount(any(CreateBankAccountRequest.class)))
                    .thenReturn(bankAccountResponse);

            ResponseEntity<BankAccountResponse> response = bankAccountController._createAccount(createBankAccountRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertNotNull(response.getBody());
            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(bankAccountResponse);
        }

        @Test
        @DisplayName("Should throw exception when create request is invalid")
        void shouldThrowExceptionWhenCreateRequestIsInvalid() {
            CreateBankAccountRequest invalidRequest = new CreateBankAccountRequest();

            String expectedMessage = "Invalid bank account request";
            when(bankAccountService.createBankAccount(any(CreateBankAccountRequest.class)))
                    .thenThrow(new IllegalArgumentException(expectedMessage));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                bankAccountController._createAccount(invalidRequest);
            });

            assertThat(exception.getMessage()).contains(expectedMessage);
        }
    }

    @Nested
    class FetchBankAccountTests {

        @Test
        @DisplayName("Should fetch bank account by account number successfully")
        void shouldFetchBankAccountByAccountNumberSuccessfully() {

            String accountNumber = "01234567";
            when(bankAccountService.fetchByAccountNumber(accountNumber))
                    .thenReturn(bankAccountResponse);

            ResponseEntity<BankAccountResponse> response = bankAccountController._fetchAccountByAccountNumber(accountNumber);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertNotNull(response.getBody());
            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(bankAccountResponse);
            assertThat(response.getBody().getAccountNumber()).isEqualTo("01234567");
        }

        @Test
        void shouldThrowExceptionWhenAccountNumberIsNotFound() {
            String accountNumber = "99999999";
            when(bankAccountService.fetchByAccountNumber(accountNumber))
                    .thenThrow(new IllegalArgumentException("Bank account not found"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                bankAccountController._fetchAccountByAccountNumber(accountNumber);
            });

            assertThat(exception.getMessage()).contains("Bank account not found");
        }

    }

    private BankAccountResponse createMockBankAccountResponse() {
        BankAccountResponse response = new BankAccountResponse();
        response.setAccountNumber("01234567");
        response.setName("Personal Savings Account");
        response.setAccountType(BankAccountResponse.AccountTypeEnum.PERSONAL);
        response.setBalance(0.0);
        response.setCurrency(BankAccountResponse.CurrencyEnum.GBP);
        response.setSortCode(BankAccountResponse.SortCodeEnum._10_10_10);
        response.setCreatedTimestamp(OffsetDateTime.now());
        response.setUpdatedTimestamp(OffsetDateTime.now());
        return response;
    }
}
