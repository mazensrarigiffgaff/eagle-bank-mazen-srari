package com.eaglebank.eagle_bank_api.service;

import com.eaglebank.eagle_bank_api.exception.BadUserRequestException;
import com.eaglebank.eagle_bank_api.exception.UserNotFoundException;
import com.eaglebank.eagle_bank_api.model.UserEntity;
import com.eaglebank.eagle_bank_api.repository.UserRepository;
import com.example.project.model.CreateUserRequest;
import com.example.project.model.CreateUserRequestAddress;
import com.example.project.model.UserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.objectMapper = new ObjectMapper(); // Ideally, inject via constructor
    }

    public UserResponse createUser(CreateUserRequest createUserRequest) {
        if (createUserRequest == null) {
            throw new BadUserRequestException("Bad request: create user request must be valid");
        }

        validateCreateUserRequest(createUserRequest);

        UserEntity savedEntity = userRepository.save(convertToEntity(createUserRequest));
        return convertToDTOResponse(savedEntity);
    }

    public UserResponse fetchUserById(String userId) {
        Long id = parseUserId(userId);

        UserEntity userEntity = userRepository
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        return convertToDTOResponse(userEntity);
    }

    public void deleteUser(String userId) {
        Long id = parseUserId(userId);

        UserEntity userEntity = userRepository
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found while attempting deletion. User ID: " + userId));

        userRepository.delete(userEntity);
    }

    private Long parseUserId(String userId) {
        if (userId == null || !userId.startsWith("usr-")) {
            throw new IllegalArgumentException("Invalid user ID format. Expected format: usr-<number>");
        }

        String numericPart = userId.substring(4);

        try {
            return Long.parseLong(numericPart);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric part of user ID: " + userId, e);
        }
    }

    private UserEntity convertToEntity(CreateUserRequest request) {
        return UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(serializeAddress(request.getAddress()))
                .build();
    }

    private UserResponse convertToDTOResponse(UserEntity entity) {
        UserResponse response = new UserResponse();
        response.setId(String.valueOf(entity.getId()));
        response.setName(entity.getName());
        response.setEmail(entity.getEmail());
        response.setPhoneNumber(entity.getPhoneNumber());
        response.setAddress(deserializeAddress(entity.getAddress()));
        response.setCreatedTimestamp(entity.getCreatedTimestamp());
        response.setUpdatedTimestamp(entity.getUpdatedTimestamp());
        return response;
    }

    private String serializeAddress(CreateUserRequestAddress address) {
        try {
            return objectMapper.writeValueAsString(address);
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize address", e);
        }
    }

    private CreateUserRequestAddress deserializeAddress(String addressJson) {
        try {
            return objectMapper.readValue(addressJson, CreateUserRequestAddress.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize address", e);
        }
    }

    private void validateCreateUserRequest(CreateUserRequest request) {
        List<String> errors = new ArrayList<>();

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            errors.add("Name is required and cannot be empty");
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            errors.add("Email is required and cannot be empty");
        } else if (!isValidEmail(request.getEmail())) {
            errors.add("Email format is invalid");
        }

        if (request.getPhoneNumber() == null || request.getPhoneNumber().trim().isEmpty()) {
            errors.add("Phone number is required and cannot be empty");
        } else if (!isValidPhoneNumber(request.getPhoneNumber())) {
            errors.add("Phone number format is invalid. Expected format: +[country_code][number]");
        }

        if (request.getAddress() == null) {
            errors.add("Address is required");
        } else {
            validateAddress(request.getAddress(), errors);
        }

        if (!errors.isEmpty()) {
            throw new BadUserRequestException("Validation failed: " + String.join(", ", errors));
        }
    }

    private void validateAddress(CreateUserRequestAddress address, List<String> errors) {
        if (address.getLine1() == null || address.getLine1().trim().isEmpty()) {
            errors.add("Address line 1 is required");
        }
        if (address.getTown() == null || address.getTown().trim().isEmpty()) {
            errors.add("Town is required");
        }
        if (address.getCounty() == null || address.getCounty().trim().isEmpty()) {
            errors.add("County is required");
        }
        if (address.getPostcode() == null || address.getPostcode().trim().isEmpty()) {
            errors.add("Postcode is required");
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^\\+[1-9]\\d{1,14}$");
    }
}
