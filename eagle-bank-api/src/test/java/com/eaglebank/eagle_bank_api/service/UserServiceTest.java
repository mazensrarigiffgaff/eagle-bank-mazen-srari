package com.eaglebank.eagle_bank_api.service;

import com.eaglebank.eagle_bank_api.exception.BadUserRequestException;
import com.eaglebank.eagle_bank_api.exception.UserNotFoundException;
import com.eaglebank.eagle_bank_api.model.UserEntity;
import com.eaglebank.eagle_bank_api.repository.UserRepository;
import com.example.project.model.CreateUserRequest;
import com.example.project.model.CreateUserRequestAddress;
import com.example.project.model.UserResponse;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private CreateUserRequest createUserRequest;
    private UserEntity savedEntity;

    @BeforeEach
    void setUp() {
        CreateUserRequestAddress address = new CreateUserRequestAddress();
        address.setLine1("123 Main St");
        address.setTown("London");
        address.setCounty("Greater London");
        address.setPostcode("E1 6AN");

        createUserRequest = new CreateUserRequest();
        createUserRequest.setName("Jane Doe");
        createUserRequest.setEmail("jane@example.com");
        createUserRequest.setPhoneNumber("+441234567890");
        createUserRequest.setAddress(address);

        savedEntity = new UserEntity();
        savedEntity.setId(1L);
        savedEntity.setName("Jane Doe");
        savedEntity.setEmail("jane@example.com");
        savedEntity.setPhoneNumber("+441234567890");
        savedEntity.setAddress("{\"line1\":\"123 Main St\",\"town\":\"London\",\"county\":\"Greater London\",\"postcode\":\"E1 6AN\"}");
        savedEntity.setCreatedTimestamp(OffsetDateTime.now());
        savedEntity.setUpdatedTimestamp(OffsetDateTime.now());
    }

    @Nested
    class CreateUserTests {
        @Test
        @DisplayName("Should create user successfully")
        void createUserSuccessfully() {
            when(userRepository.save(any(UserEntity.class))).thenReturn(savedEntity);

            UserResponse response = userService.createUser(createUserRequest);

            assertThat(response.getId()).isEqualTo("1");
            assertThat(response.getName()).isEqualTo("Jane Doe");
            assertThat(response.getEmail()).isEqualTo("jane@example.com");
            assertThat(response.getPhoneNumber()).isEqualTo("+441234567890");
            assertThat(response.getAddress().getLine1()).isEqualTo("123 Main St");

            ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
            verify(userRepository).save(captor.capture());

            UserEntity captured = captor.getValue();
            assertThat(captured.getName()).isEqualTo("Jane Doe");
            assertThat(captured.getAddress()).contains("Main St");
        }

        @Test
        void createUserThrowsForNullRequest() {
            assertThatThrownBy(() -> userService.createUser(null))
                    .isInstanceOf(BadUserRequestException.class)
                    .hasMessageContaining("Bad request: create user request must be valid");
        }
    }

    @Nested
    class FetchUserByIdTests {
        @Test
        void fetchUserByIdSuccessfully() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(savedEntity));

            UserResponse response = userService.fetchUserById("usr-1");

            assertThat(response.getId()).isEqualTo("1");
            assertThat(response.getName()).isEqualTo("Jane Doe");
            assertThat(response.getAddress().getPostcode()).isEqualTo("E1 6AN");
        }

        @Test
        void fetchUserByIdThrowsForInvalidPrefix() {
            assertThatThrownBy(() -> userService.fetchUserById("abc-123"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid user ID format");
        }

        @Test
        void fetchUserByIdThrowsForNonNumericId() {
            assertThatThrownBy(() -> userService.fetchUserById("usr-abc"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid numeric part");
        }

        @Test
        void fetchUserByIdThrowsWhenUserNotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.fetchUserById("usr-99"))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("User not found with ID: usr-99");
        }
    }

    @Nested
    class DeleteUserTests {
        @Test
        void deleteUserSuccessfully() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(savedEntity));

            userService.deleteUser("usr-1");

            verify(userRepository).delete(savedEntity);
        }

        @Test
        void deleteUserThrowsWhenUserNotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.deleteUser("usr-99"))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("User not found while attempting deletion. User ID: usr-99");
        }
    }

}
