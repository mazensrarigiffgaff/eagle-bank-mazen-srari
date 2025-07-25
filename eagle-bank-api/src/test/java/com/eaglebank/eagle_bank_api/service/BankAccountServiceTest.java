package com.eaglebank.eagle_bank_api.service;

import com.eaglebank.eagle_bank_api.model.BankAccountEntity;
import com.eaglebank.eagle_bank_api.repository.BankAccountRepository;
import com.example.project.model.BankAccountResponse;
import com.example.project.model.CreateBankAccountRequest;
import com.example.project.model.UpdateBankAccountRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BankAccountService Tests")
class BankAccountServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @InjectMocks
    private BankAccountService bankAccountService;

    private CreateBankAccountRequest createBankAccountRequest;
    private BankAccountEntity bankAccountEntity;

    @BeforeEach
    void setUp() {
        createBankAccountRequest = new CreateBankAccountRequest();
        createBankAccountRequest.setName("My Personal Bank Account");
        createBankAccountRequest.setAccountType(CreateBankAccountRequest.AccountTypeEnum.PERSONAL);

        bankAccountEntity = BankAccountEntity.builder()
                .id(1L)
                .accountNumber("01234567")
                .sortCode(BankAccountResponse.SortCodeEnum._10_10_10)
                .name("My Personal Bank Account")
                .accountType(BankAccountResponse.AccountTypeEnum.PERSONAL)
                .balance(0.0)
                .currency(BankAccountResponse.CurrencyEnum.GBP)
                .createdTimestamp(OffsetDateTime.now())
                .updatedTimestamp(OffsetDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Create Bank Account Tests")
    class CreateBankAccountTests {

        @Test
        @DisplayName("Should create bank account successfully")
        void shouldCreateBankAccountSuccessfully() {

            when(bankAccountRepository.save(any(BankAccountEntity.class))).thenReturn(bankAccountEntity);
            when(bankAccountRepository.findByAccountNumber(anyString())).thenReturn(Optional.empty());

            BankAccountResponse response = bankAccountService.createBankAccount(createBankAccountRequest);

            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo(createBankAccountRequest.getName());
            assertThat(response.getAccountType()).isEqualTo(BankAccountResponse.AccountTypeEnum.PERSONAL);
            assertThat(response.getSortCode()).isEqualTo(BankAccountResponse.SortCodeEnum._10_10_10);
            assertThat(response.getBalance()).isEqualTo(0.0);
            assertThat(response.getCurrency()).isEqualTo(BankAccountResponse.CurrencyEnum.GBP);

            ArgumentCaptor<BankAccountEntity> captor = ArgumentCaptor.forClass(BankAccountEntity.class);
            verify(bankAccountRepository).save(captor.capture());

            BankAccountEntity captured = captor.getValue();
            assertThat(captured.getName()).isEqualTo("My Personal Bank Account");
            assertThat(captured.getAccountNumber()).startsWith("01");
            assertThat(captured.getAccountNumber()).hasSize(8);
        }

        @Test
        @DisplayName("Should generate unique account number when first one exists")
        void shouldGenerateUniqueAccountNumberWhenFirstOneExists() {
            when(bankAccountRepository.findByAccountNumber(anyString()))
                    .thenReturn(Optional.of(bankAccountEntity))
                    .thenReturn(Optional.empty());
            when(bankAccountRepository.save(any(BankAccountEntity.class))).thenReturn(bankAccountEntity);

            BankAccountResponse response = bankAccountService.createBankAccount(createBankAccountRequest);

            assertThat(response).isNotNull();
            verify(bankAccountRepository).save(any(BankAccountEntity.class));
        }

        @Test
        @DisplayName("Should throw exception when repository save fails")
        void shouldThrowExceptionWhenRepositorySaveFails() {
            when(bankAccountRepository.findByAccountNumber(anyString())).thenReturn(Optional.empty());
            when(bankAccountRepository.save(any(BankAccountEntity.class)))
                    .thenThrow(new RuntimeException("Database connection failed"));

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                bankAccountService.createBankAccount(createBankAccountRequest);
            });

            assertThat(exception.getMessage()).contains("Database connection failed");
        }
    }

    @Nested
    @DisplayName("Fetch Bank Account Tests")
    class FetchBankAccountTests {

        @Test
        @DisplayName("Should fetch bank account by account number successfully")
        void shouldFetchBankAccountByAccountNumberSuccessfully() {
            String accountNumber = "01234567";

            bankAccountEntity.setAccountNumber(accountNumber);
            when(bankAccountRepository.findByAccountNumber(accountNumber))
                    .thenReturn(Optional.of(bankAccountEntity));

            BankAccountResponse response = bankAccountService.fetchByAccountNumber(accountNumber);

            assertThat(response).isNotNull();
            assertThat(response.getAccountNumber()).isEqualTo(accountNumber);
            assertThat(response.getName()).isEqualTo("My Personal Bank Account");
            assertThat(response.getAccountType()).isEqualTo(BankAccountResponse.AccountTypeEnum.PERSONAL);
            assertThat(response.getBalance()).isEqualTo(0.0);
            assertThat(response.getCurrency()).isEqualTo(BankAccountResponse.CurrencyEnum.GBP);
            assertThat(response.getSortCode()).isEqualTo(BankAccountResponse.SortCodeEnum._10_10_10);
        }

        @Test
        @DisplayName("Should throw exception when account not found")
        void shouldThrowExceptionWhenAccountNotFound() {
            String nonExistentAccountNumber = "01999999";

            when(bankAccountRepository.findByAccountNumber(nonExistentAccountNumber))
                    .thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                bankAccountService.fetchByAccountNumber(nonExistentAccountNumber);
            });

            assertThat(exception.getMessage())
                    .contains("Bank account not found with account number: " + nonExistentAccountNumber);
        }
    }

    @Nested
    @DisplayName("Delete Bank Account Tests")
    class DeleteBankAccountTests {
        @Test
        @DisplayName("Should delete bank account by account number successfully")
        void shouldDeleteBankAccountByAccountNumberSuccessfully() {
            String accountNumber = "01234567";

            when(bankAccountRepository.findByAccountNumber(accountNumber))
                    .thenReturn(Optional.of(bankAccountEntity));

            bankAccountService.deleteBankAccount(accountNumber);

            verify(bankAccountRepository).delete(bankAccountEntity);
        }

        @Test
        @DisplayName("Should throw exception when trying to delete non-existent account")
        void shouldThrowExceptionWhenTryingToDeleteNonExistentAccount() {
            String nonExistentAccountNumber = "01999999";

            when(bankAccountRepository.findByAccountNumber(nonExistentAccountNumber))
                    .thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                bankAccountService.deleteBankAccount(nonExistentAccountNumber);
            });

            assertThat(exception.getMessage())
                    .contains("Bank account not found while attempting deletion. Account number: " + nonExistentAccountNumber);
        }
    }

    @Nested
    @DisplayName("Update Bank Account Tests")
    class UpdateBankAccountTests {
        @Test
        @DisplayName("Should update bank account successfully")
        void shouldUpdateBankAccountSuccessfully() {
            String accountNumber = "01234567";
            String name = "Updated Name";

            UpdateBankAccountRequest updateRequest = new UpdateBankAccountRequest();
            updateRequest.setName(name);
            updateRequest.setAccountType(UpdateBankAccountRequest.AccountTypeEnum.PERSONAL);

            when(bankAccountRepository.findByAccountNumber(accountNumber))
                    .thenReturn(Optional.of(bankAccountEntity));

            when(bankAccountRepository.save(any(BankAccountEntity.class)))
                    .thenReturn(bankAccountEntity);

            BankAccountResponse response = bankAccountService.updateBankAccount(accountNumber, updateRequest);

            assertThat(response.getName()).isEqualTo(name);
            assertThat(response.getAccountType()).isEqualTo(BankAccountResponse.AccountTypeEnum.PERSONAL);
        }

        @Test
        @DisplayName("Should throw exception when trying to update non-existent account")
        void shouldThrowExceptionWhenTryingToUpdateNonExistentAccount() {
            String nonExistentAccountNumber = "01999999";
            UpdateBankAccountRequest updateRequest = new UpdateBankAccountRequest();
            updateRequest.setName("Updated Name");

            when(bankAccountRepository.findByAccountNumber(nonExistentAccountNumber))
                    .thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                bankAccountService.updateBankAccount(nonExistentAccountNumber, updateRequest);
            });

            assertThat(exception.getMessage())
                    .contains("Bank account not found with account number: " + nonExistentAccountNumber);
        }
    }

    @Nested
    @DisplayName("Helper Method Tests")
    class HelperMethodTests {

        @Test
        @DisplayName("Should convert entity to response correctly")
        void shouldConvertEntityToResponseCorrectly() {
            when(bankAccountRepository.findByAccountNumber("01234567"))
                    .thenReturn(Optional.of(bankAccountEntity));

            BankAccountResponse response = bankAccountService.fetchByAccountNumber("01234567");

            assertThat(response.getAccountNumber()).isEqualTo(bankAccountEntity.getAccountNumber());
            assertThat(response.getName()).isEqualTo(bankAccountEntity.getName());
            assertThat(response.getAccountType().getValue()).isEqualTo(bankAccountEntity.getAccountType().getValue());
            assertThat(response.getBalance()).isEqualTo(bankAccountEntity.getBalance());
            assertThat(response.getCurrency().getValue()).isEqualTo(bankAccountEntity.getCurrency().getValue());
            assertThat(response.getSortCode().getValue()).isEqualTo(bankAccountEntity.getSortCode().getValue());
        }

        @Test
        @DisplayName("Should generate account number with correct format")
        void shouldGenerateAccountNumberWithCorrectFormat() {
            when(bankAccountRepository.findByAccountNumber(anyString())).thenReturn(Optional.empty());
            when(bankAccountRepository.save(any(BankAccountEntity.class))).thenReturn(bankAccountEntity);

            bankAccountService.createBankAccount(createBankAccountRequest);

            ArgumentCaptor<BankAccountEntity> captor = ArgumentCaptor.forClass(BankAccountEntity.class);
            verify(bankAccountRepository).save(captor.capture());

            String generatedAccountNumber = captor.getValue().getAccountNumber();
            assertThat(generatedAccountNumber).startsWith("01");
            assertThat(generatedAccountNumber).hasSize(8);
            assertThat(generatedAccountNumber).matches("^01\\d{6}$");
        }
    }
}