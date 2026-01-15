package com.example.userms.controller;

import com.example.userms.dto.*;
import com.example.userms.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "User CRUD operations and permission management")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    @Operation(summary = "Create a new user", description = "Create a new user (Admin only)")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        UserResponse response = userService.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve user details by ID")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve all users")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update user details (User can update themselves or Admin can update anyone)")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user (Admin only)")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/permissions/grant")
    @Operation(summary = "Grant permissions", description = "Grant permissions to a user (Admin only)")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<UserResponse> grantPermissions(
            @PathVariable Long id,
            @Valid @RequestBody PermissionRequest request) {
        request.setUserId(id);
        UserResponse response = userService.grantPermissions(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/permissions/revoke")
    @Operation(summary = "Revoke permissions", description = "Revoke permissions from a user (Admin only)")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<UserResponse> revokePermissions(
            @PathVariable Long id,
            @Valid @RequestBody PermissionRequest request) {
        request.setUserId(id);
        UserResponse response = userService.revokePermissions(id, request);
        return ResponseEntity.ok(response);
    }
}
