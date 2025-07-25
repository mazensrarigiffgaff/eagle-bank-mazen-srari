package com.eaglebank.eagle_bank_api.service;

import com.eaglebank.eagle_bank_api.exception.BadBankAccountRequestException;
import com.eaglebank.eagle_bank_api.repository.BankAccountRepository;
import com.example.project.model.CreateBankAccountRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceValidationTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @InjectMocks
    private BankAccountService bankAccountService;

    private CreateBankAccountRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new CreateBankAccountRequest();
        validRequest.setName("My Personal Account");
        validRequest.setAccountType(CreateBankAccountRequest.AccountTypeEnum.PERSONAL);
    }

    @Nested
    @DisplayName("Create Bank Account Validation Tests")
    class CreateBankAccountValidationTests {

        @Test
        @DisplayName("Should throw exception when request is null")
        void shouldThrowExceptionWhenRequestIsNull() {
            assertThatThrownBy(() -> bankAccountService.createBankAccount(null))
                    .isInstanceOf(BadBankAccountRequestException.class)
                    .hasMessageContaining("Bad request: create bank account request must be valid");
        }

        @Test
        @DisplayName("Should throw exception when name is null")
        void shouldThrowExceptionWhenNameIsNull() {
            validRequest.setName(null);

            assertThatThrownBy(() -> bankAccountService.createBankAccount(validRequest))
                    .isInstanceOf(BadBankAccountRequestException.class)
                    .hasMessageContaining("Name is required and cannot be empty");
        }

        @Test
        @DisplayName("Should throw exception when name is empty")
        void shouldThrowExceptionWhenNameIsEmpty() {
            validRequest.setName("");

            assertThatThrownBy(() -> bankAccountService.createBankAccount(validRequest))
                    .isInstanceOf(BadBankAccountRequestException.class)
                    .hasMessageContaining("Name is required and cannot be empty");
        }

        @Test
        @DisplayName("Should throw exception when name is whitespace only")
        void shouldThrowExceptionWhenNameIsWhitespaceOnly() {
            validRequest.setName("   ");

            assertThatThrownBy(() -> bankAccountService.createBankAccount(validRequest))
                    .isInstanceOf(BadBankAccountRequestException.class)
                    .hasMessageContaining("Name is required and cannot be empty");
        }

        @Test
        @DisplayName("Should throw exception when name exceeds 100 characters")
        void shouldThrowExceptionWhenNameExceeds100Characters() {
            String longName = "a".repeat(101);
            validRequest.setName(longName);

            assertThatThrownBy(() -> bankAccountService.createBankAccount(validRequest))
                    .isInstanceOf(BadBankAccountRequestException.class)
                    .hasMessageContaining("Name cannot exceed 100 characters");
        }

        @Test
        @DisplayName("Should throw exception when account type is null")
        void shouldThrowExceptionWhenAccountTypeIsNull() {
            validRequest.setAccountType(null);

            assertThatThrownBy(() -> bankAccountService.createBankAccount(validRequest))
                    .isInstanceOf(BadBankAccountRequestException.class)
                    .hasMessageContaining("Account type is required");
        }

        @Test
        @DisplayName("Should include all validation errors in exception message")
        void shouldIncludeAllValidationErrorsInExceptionMessage() {
            validRequest.setName("");
            validRequest.setAccountType(null);

            assertThatThrownBy(() -> bankAccountService.createBankAccount(validRequest))
                    .isInstanceOf(BadBankAccountRequestException.class)
                    .hasMessageContaining("Name is required")
                    .hasMessageContaining("Account type is required");
        }
    }

    @Nested
    @DisplayName("Fetch Bank Account Validation Tests")
    class FetchBankAccountValidationTests {

        @Test
        @DisplayName("Should throw exception when account number is null")
        void shouldThrowExceptionWhenAccountNumberIsNull() {
            assertThatThrownBy(() -> bankAccountService.fetchByAccountNumber(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Account number is required");
        }

        @Test
        @DisplayName("Should throw exception when account number is empty")
        void shouldThrowExceptionWhenAccountNumberIsEmpty() {
            assertThatThrownBy(() -> bankAccountService.fetchByAccountNumber(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Account number is required");
        }

        @Test
        @DisplayName("Should throw exception when account number is whitespace only")
        void shouldThrowExceptionWhenAccountNumberIsWhitespaceOnly() {
            assertThatThrownBy(() -> bankAccountService.fetchByAccountNumber("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Account number is required");
        }

        @Test
        @DisplayName("Should throw exception when account number format is invalid - wrong prefix")
        void shouldThrowExceptionWhenAccountNumberFormatIsInvalidWrongPrefix() {
            assertThatThrownBy(() -> bankAccountService.fetchByAccountNumber("02123456"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid account number format");
        }

        @Test
        @DisplayName("Should throw exception when account number format is invalid - too short")
        void shouldThrowExceptionWhenAccountNumberFormatIsInvalidTooShort() {
            assertThatThrownBy(() -> bankAccountService.fetchByAccountNumber("0112345"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid account number format");
        }

        @Test
        @DisplayName("Should throw exception when account number format is invalid - too long")
        void shouldThrowExceptionWhenAccountNumberFormatIsInvalidTooLong() {
            assertThatThrownBy(() -> bankAccountService.fetchByAccountNumber("011234567"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid account number format");
        }

        @Test
        @DisplayName("Should throw exception when account number format is invalid - contains letters")
        void shouldThrowExceptionWhenAccountNumberFormatIsInvalidContainsLetters() {
            assertThatThrownBy(() -> bankAccountService.fetchByAccountNumber("01abc123"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid account number format");
        }
    }
}
