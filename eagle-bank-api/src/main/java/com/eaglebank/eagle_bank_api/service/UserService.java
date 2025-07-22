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

    // === Private Helpers ===

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
        UserEntity entity = new UserEntity();
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());
        entity.setPhoneNumber(request.getPhoneNumber());
        entity.setAddress(serializeAddress(request.getAddress()));
        return entity;
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
}
