package com.eaglebank.eagle_bank_api.service;

import com.eaglebank.eagle_bank_api.exception.BadUserRequestException;
import com.example.project.model.CreateUserRequest;
import com.example.project.model.CreateUserRequestAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class UserServiceValidationTest {

    @InjectMocks
    private UserService userService;

    private CreateUserRequest validRequest;

    @BeforeEach
    void setUp() {
        CreateUserRequestAddress validAddress = new CreateUserRequestAddress();
        validAddress.setLine1("123 Main St");
        validAddress.setTown("London");
        validAddress.setCounty("Greater London");
        validAddress.setPostcode("E1 6AN");

        validRequest = new CreateUserRequest();
        validRequest.setName("Jane Doe");
        validRequest.setEmail("jane@example.com");
        validRequest.setPhoneNumber("+441234567890");
        validRequest.setAddress(validAddress);
    }

    @Nested
    @DisplayName("Name Validation Tests")
    class NameValidationTests {

        @Test
        @DisplayName("Should throw exception when name is null")
        void shouldThrowExceptionWhenNameIsNull() {
            validRequest.setName(null);

            assertThatThrownBy(() -> userService.createUser(validRequest))
                    .isInstanceOf(BadUserRequestException.class)
                    .hasMessageContaining("Name is required and cannot be empty");
        }

        @Test
        @DisplayName("Should throw exception when name is empty")
        void shouldThrowExceptionWhenNameIsEmpty() {
            validRequest.setName("");

            assertThatThrownBy(() -> userService.createUser(validRequest))
                    .isInstanceOf(BadUserRequestException.class)
                    .hasMessageContaining("Name is required and cannot be empty");
        }

        @Test
        @DisplayName("Should throw exception when name is whitespace only")
        void shouldThrowExceptionWhenNameIsWhitespaceOnly() {
            validRequest.setName("   ");

            assertThatThrownBy(() -> userService.createUser(validRequest))
                    .isInstanceOf(BadUserRequestException.class)
                    .hasMessageContaining("Name is required and cannot be empty");
        }
    }

    @Nested
    @DisplayName("Email Validation Tests")
    class EmailValidationTests {

        @Test
        @DisplayName("Should throw exception when email is null")
        void shouldThrowExceptionWhenEmailIsNull() {
            validRequest.setEmail(null);

            assertThatThrownBy(() -> userService.createUser(validRequest))
                    .isInstanceOf(BadUserRequestException.class)
                    .hasMessageContaining("Email is required and cannot be empty");
        }

        @Test
        @DisplayName("Should throw exception when email is empty")
        void shouldThrowExceptionWhenEmailIsEmpty() {
            validRequest.setEmail("");

            assertThatThrownBy(() -> userService.createUser(validRequest))
                    .isInstanceOf(BadUserRequestException.class)
                    .hasMessageContaining("Email is required and cannot be empty");
        }

        @Test
        @DisplayName("Should throw exception when email format is invalid")
        void shouldThrowExceptionWhenEmailFormatIsInvalid() {
            validRequest.setEmail("invalid-email");

            assertThatThrownBy(() -> userService.createUser(validRequest))
                    .isInstanceOf(BadUserRequestException.class)
                    .hasMessageContaining("Email format is invalid");
        }

        @Test
        @DisplayName("Should throw exception when email is missing @ symbol")
        void shouldThrowExceptionWhenEmailIsMissingAtSymbol() {
            validRequest.setEmail("testexample.com");

            assertThatThrownBy(() -> userService.createUser(validRequest))
                    .isInstanceOf(BadUserRequestException.class)
                    .hasMessageContaining("Email format is invalid");
        }

        @Test
        @DisplayName("Should throw exception when email is missing domain")
        void shouldThrowExceptionWhenEmailIsMissingDomain() {
            validRequest.setEmail("test@");

            assertThatThrownBy(() -> userService.createUser(validRequest))
                    .isInstanceOf(BadUserRequestException.class)
                    .hasMessageContaining("Email format is invalid");
        }
    }

    @Nested
    @DisplayName("Phone Number Validation Tests")
    class PhoneNumberValidationTests {

        @Test
        @DisplayName("Should throw exception when phone number is null")
        void shouldThrowExceptionWhenPhoneNumberIsNull() {
            validRequest.setPhoneNumber(null);

            assertThatThrownBy(() -> userService.createUser(validRequest))
                    .isInstanceOf(BadUserRequestException.class)
                    .hasMessageContaining("Phone number is required and cannot be empty");
        }

        @Test
        @DisplayName("Should throw exception when phone number is empty")
        void shouldThrowExceptionWhenPhoneNumberIsEmpty() {
            validRequest.setPhoneNumber("");

            assertThatThrownBy(() -> userService.createUser(validRequest))
                    .isInstanceOf(BadUserRequestException.class)
                    .hasMessageContaining("Phone number is required and cannot be empty");
        }

        @Test
        @DisplayName("Should throw exception when phone number is missing country code")
        void shouldThrowExceptionWhenPhoneNumberIsMissingCountryCode() {
            validRequest.setPhoneNumber("1234567890");

            assertThatThrownBy(() -> userService.createUser(validRequest))
                    .isInstanceOf(BadUserRequestException.class)
                    .hasMessageContaining("Phone number format is invalid");
        }

        @Test
        @DisplayName("Should throw exception when phone number starts with invalid country code")
        void shouldThrowExceptionWhenPhoneNumberStartsWithInvalidCountryCode() {
            validRequest.setPhoneNumber("+01234567890");

            assertThatThrownBy(() -> userService.createUser(validRequest))
                    .isInstanceOf(BadUserRequestException.class)
                    .hasMessageContaining("Phone number format is invalid");
        }

        @Test
        @DisplayName("Should throw exception when phone number is too short")
        void shouldThrowExceptionWhenPhoneNumberIsTooShort() {
            validRequest.setPhoneNumber("+4");

            assertThatThrownBy(() -> userService.createUser(validRequest))
                    .isInstanceOf(BadUserRequestException.class)
                    .hasMessageContaining("Phone number format is invalid");
        }

        @Test
        @DisplayName("Should throw exception when phone number is too long")
        void shouldThrowExceptionWhenPhoneNumberIsTooLong() {
            validRequest.setPhoneNumber("+44123456789012345678");

            assertThatThrownBy(() -> userService.createUser(validRequest))
                    .isInstanceOf(BadUserRequestException.class)
                    .hasMessageContaining("Phone number format is invalid");
        }
    }

    @Nested
    @DisplayName("Address Validation Tests")
    class AddressValidationTests {

        @Test
        @DisplayName("Should throw exception when address is null")
        void shouldThrowExceptionWhenAddressIsNull() {
            validRequest.setAddress(null);

            assertThatThrownBy(() -> userService.createUser(validRequest))
                    .isInstanceOf(BadUserRequestException.class)
                    .hasMessageContaining("Address is required");
        }

        @Test
        @DisplayName("Should throw exception when address line1 is missing")
        void shouldThrowExceptionWhenAddressLine1IsMissing() {
            validRequest.getAddress().setLine1(null);

            assertThatThrownBy(() -> userService.createUser(validRequest))
                    .isInstanceOf(BadUserRequestException.class)
                    .hasMessageContaining("Address line 1 is required");
        }

        @Test
        @DisplayName("Should throw exception when town is missing")
        void shouldThrowExceptionWhenTownIsMissing() {
            validRequest.getAddress().setTown("");

            assertThatThrownBy(() -> userService.createUser(validRequest))
                    .isInstanceOf(BadUserRequestException.class)
                    .hasMessageContaining("Town is required");
        }

        @Test
        @DisplayName("Should throw exception when county is missing")
        void shouldThrowExceptionWhenCountyIsMissing() {
            validRequest.getAddress().setCounty(null);

            assertThatThrownBy(() -> userService.createUser(validRequest))
                    .isInstanceOf(BadUserRequestException.class)
                    .hasMessageContaining("County is required");
        }

        @Test
        @DisplayName("Should throw exception when postcode is missing")
        void shouldThrowExceptionWhenPostcodeIsMissing() {
            validRequest.getAddress().setPostcode("   ");

            assertThatThrownBy(() -> userService.createUser(validRequest))
                    .isInstanceOf(BadUserRequestException.class)
                    .hasMessageContaining("Postcode is required");
        }
    }

    @Nested
    @DisplayName("Multiple Validation Error Tests")
    class MultipleValidationErrorTests {

        @Test
        @DisplayName("Should include all validation errors in exception message")
        void shouldIncludeAllValidationErrorsInExceptionMessage() {
            CreateUserRequest invalidRequest = new CreateUserRequest();
            invalidRequest.setName("");
            invalidRequest.setEmail("invalid-email");
            invalidRequest.setPhoneNumber("123");
            invalidRequest.setAddress(null);

            assertThatThrownBy(() -> userService.createUser(invalidRequest))
                    .isInstanceOf(BadUserRequestException.class)
                    .hasMessageContaining("Name is required")
                    .hasMessageContaining("Email format is invalid")
                    .hasMessageContaining("Phone number format is invalid")
                    .hasMessageContaining("Address is required");
        }
    }
}
