package com.eaglebank.eagle_bank_api.controller;

import com.eaglebank.eagle_bank_api.exception.UserNotFoundException;
import com.eaglebank.eagle_bank_api.service.UserService;
import com.example.project.model.CreateUserRequest;
import com.example.project.model.UserResponse;
import org.junit.jupiter.api.BeforeEach;
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
        void createNewUserSuccessfully() {
            when(userService.createUser(any(CreateUserRequest.class))).thenReturn(userResponse);

            ResponseEntity<UserResponse> response = userController._createUser(createUserRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertNotNull(response.getBody());
            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(userResponse);
        }

        @Test
        void createNewUserThrowsExceptionWhenRequestHasInvalidName() {

            CreateUserRequest invalidRequest = new CreateUserRequest();
            invalidRequest.setName("");

            String expectedMessage = "Invalid user request";

            when(userService.createUser(any(CreateUserRequest.class)))
                    .thenThrow(new IllegalArgumentException(expectedMessage));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userController._createUser(invalidRequest);
            });

            assertThat(exception.getMessage()).contains(expectedMessage);
        }

        @Test
        void createNewUserThrowsExceptionWhenRequestHasInvalidEmail() {

            CreateUserRequest invalidRequest = new CreateUserRequest();
            invalidRequest.setName("John Doe");
            invalidRequest.setEmail("");

            String expectedMessage = "Invalid user request";

            when(userService.createUser(any(CreateUserRequest.class)))
                    .thenThrow(new IllegalArgumentException(expectedMessage));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userController._createUser(invalidRequest);
            });

            assertThat(exception.getMessage()).contains(expectedMessage);
        }

        @Test
        void createNewUserThrowsExceptionWhenRequestHasInvalidPhoneNumber() {

            CreateUserRequest invalidRequest = new CreateUserRequest();
            invalidRequest.setName("John Doe");
            invalidRequest.setEmail("john.doe@gmail.com");
            invalidRequest.setPhoneNumber("");

            String expectedMessage = "Invalid user request";

            when(userService.createUser(any(CreateUserRequest.class)))
                    .thenThrow(new IllegalArgumentException(expectedMessage));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                        userController._createUser(invalidRequest);
                    }
            );

            assertThat(exception.getMessage()).contains(expectedMessage);
        }

    }


    @Nested
    class FetchUserByIDTests {

        @Test
        void fetchUserByIdSuccessfully() {

            String userId = "usr-123";
            when(userService.fetchUserById(userId)).thenReturn(userResponse);

            ResponseEntity<UserResponse> response = userController._fetchUserByID(userId);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertNotNull(response.getBody());
            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(userResponse);
        }

        @Test
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
}
