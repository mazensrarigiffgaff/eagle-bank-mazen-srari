package com.eaglebank.eagle_bank_api.controller;

import com.eaglebank.eagle_bank_api.service.UserService;
import com.example.project.api.V1Api;
import com.example.project.model.CreateUserRequest;
import com.example.project.model.UserResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController implements V1Api {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/v1/users")
    @Override
    public ResponseEntity<UserResponse> _createUser(@Valid @RequestBody CreateUserRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(body));
    }

    @GetMapping("/v1/users/{userId}")
    @Override
    public ResponseEntity<UserResponse> _fetchUserByID(@PathVariable @Pattern(regexp = "^usr-[A-Za-z0-9]+$") String userId) {
        return ResponseEntity.ok(userService.fetchUserById(userId));
    }

    @DeleteMapping("/v1/users/{userId}")
    @Override
    public ResponseEntity<Void> _deleteUserByID(@PathVariable @Pattern(regexp = "^usr-[A-Za-z0-9]+$") String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

}
