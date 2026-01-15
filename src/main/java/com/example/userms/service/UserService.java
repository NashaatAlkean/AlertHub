package com.example.userms.service;

import com.example.userms.dto.*;
import com.example.userms.enums.Permission;

import java.util.List;

public interface UserService {
    
    UserResponse createUser(UserRequest request);
    
    UserResponse getUserById(Long id);
    
    List<UserResponse> getAllUsers();
    
    UserResponse updateUser(Long id, UserRequest request);
    
    void deleteUser(Long id);
    
    UserResponse grantPermissions(Long userId, PermissionRequest request);
    
    UserResponse revokePermissions(Long userId, PermissionRequest request);
    
    boolean hasPermission(Long userId, Permission permission);
    
    LoginResponse login(LoginRequest request);
}
