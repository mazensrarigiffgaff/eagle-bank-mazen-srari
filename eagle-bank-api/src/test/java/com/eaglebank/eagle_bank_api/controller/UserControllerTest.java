package com.eaglebank.eagle_bank_api.controller;

import com.eaglebank.eagle_bank_api.exception.BadUserRequestException;
import com.eaglebank.eagle_bank_api.exception.UserNotFoundException;
import com.eaglebank.eagle_bank_api.service.UserService;
import com.example.project.model.CreateUserRequest;
import com.example.project.model.UserResponse;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private UserResponse userResponse;
    private CreateUserRequest createUserRequest;

    @BeforeEach
    void setUp() {
        userResponse = new UserResponse();
        createUserRequest = new CreateUserRequest();
    }

    @Nested
    class CreateUserTests {

        @Test
        @DisplayName("Should create new user successfully")
        void createNewUserSuccessfully() {
            when(userService.createUser(any(CreateUserRequest.class))).thenReturn(userResponse);

            ResponseEntity<UserResponse> response = userController._createUser(createUserRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertNotNull(response.getBody());
            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(userResponse);
        }

        @Test
        @DisplayName("Should throw exception when user creation fails")
        void createUserThrowsExceptionWhenCreationFails() {
            String expectedMessage = "Invalid user data";
            when(userService.createUser(any(CreateUserRequest.class)))
                    .thenThrow(new BadUserRequestException(expectedMessage));

            BadUserRequestException exception = assertThrows(BadUserRequestException.class, () -> {
                userController._createUser(createUserRequest);
            });

            assertThat(exception.getMessage()).contains(expectedMessage);
        }

    }

    @Nested
    class FetchUserByIDTests {

        @Test
        @DisplayName("Should fetch user by ID successfully")
        void fetchUserByIdSuccessfully() {

            String userId = "usr-123";
            when(userService.fetchUserById(userId)).thenReturn(userResponse);

            ResponseEntity<UserResponse> response = userController._fetchUserByID(userId);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertNotNull(response.getBody());
            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(userResponse);
        }

        @Test
        @DisplayName("Should throw exception when user not found by ID")
        void fetchUserByIdThrowsExceptionWhenUserNotFound() {

            String nonExistentUserId = "usr-non-existent-user";
            String expectedMessage = "User not found with ID: " + nonExistentUserId;

            when(userService.fetchUserById(nonExistentUserId))
                    .thenThrow(new UserNotFoundException(expectedMessage));

            UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
                userController._fetchUserByID(nonExistentUserId);
            });

            assertThat(exception.getMessage()).contains(expectedMessage);
        }
    }

    @Nested
    class DeleteUserByIDTests {

        @Test
        @DisplayName("Should delete user by ID successfully")
        void deleteUserByIdSuccessfully() {

            String userId = "usr-123";

            ResponseEntity<Void> response = userController._deleteUserByID(userId);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }
}
