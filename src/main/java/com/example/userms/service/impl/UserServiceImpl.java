package com.example.userms.service.impl;

import com.example.userms.dto.*;
import com.example.userms.entity.Role;
import com.example.userms.entity.User;
import com.example.userms.entity.UserRole;
import com.example.userms.enums.Permission;
import com.example.userms.exception.RoleNotFoundException;
import com.example.userms.exception.UnauthorizedException;
import com.example.userms.exception.UserAlreadyExistsException;
import com.example.userms.exception.UserNotFoundException;
import com.example.userms.repository.RoleRepository;
import com.example.userms.repository.UserRepository;
import com.example.userms.repository.UserRoleRepository;
import com.example.userms.security.CustomUserDetails;
import com.example.userms.security.JwtUtils;
import com.example.userms.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public UserResponse createUser(UserRequest request) {
        // Check if user is admin (only admin can create users)
        checkAdminPermission();

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + request.getUsername());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists: " + request.getEmail());
        }

        // Create user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .userRoles(new HashSet<>())
                .build();

        // Save user first
        user = userRepository.save(user);

        // Add permissions if provided
        if (request.getPermissions() != null && !request.getPermissions().isEmpty()) {
            for (String permissionStr : request.getPermissions()) {
                Permission permission = Permission.fromString(permissionStr);
                Role role = roleRepository.findByRole(permission)
                        .orElseThrow(() -> new RoleNotFoundException("Role not found: " + permissionStr));
                
                UserRole userRole = UserRole.builder()
                        .user(user)
                        .role(role)
                        .build();
                user.getUserRoles().add(userRole);
            }
            user = userRepository.save(user);
        }

        return convertToResponse(user);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return convertToResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest request) {
        // Check if user is updating themselves or is admin
        checkUpdatePermission(id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        // Check if new username is taken by another user
        if (!user.getUsername().equals(request.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + request.getUsername());
        }

        // Check if new email is taken by another user
        if (!user.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists: " + request.getEmail());
        }

        // Update user fields
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        user = userRepository.save(user);
        return convertToResponse(user);
    }

    @Override
    public void deleteUser(Long id) {
        // Only admin can delete users
        checkAdminPermission();

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        // Prevent admin from deleting themselves
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        if (userDetails.getUser().getId().equals(id)) {
            throw new UnauthorizedException("Cannot delete your own account");
        }

        userRepository.delete(user);
    }

    @Override
    public UserResponse grantPermissions(Long userId, PermissionRequest request) {
        // Only admin can grant permissions
        checkAdminPermission();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        for (String permissionStr : request.getPermissions()) {
            Permission permission = Permission.fromString(permissionStr);
            Role role = roleRepository.findByRole(permission)
                    .orElseThrow(() -> new RoleNotFoundException("Role not found: " + permissionStr));

            // Check if user already has this permission
            boolean hasPermission = user.getUserRoles().stream()
                    .anyMatch(ur -> ur.getRole().getRole().equals(permission));

            if (!hasPermission) {
                UserRole userRole = UserRole.builder()
                        .user(user)
                        .role(role)
                        .build();
                user.getUserRoles().add(userRole);
            }
        }

        user = userRepository.save(user);
        return convertToResponse(user);
    }

    @Override
    public UserResponse revokePermissions(Long userId, PermissionRequest request) {
        // Only admin can revoke permissions
        checkAdminPermission();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        for (String permissionStr : request.getPermissions()) {
            Permission permission = Permission.fromString(permissionStr);
            user.getUserRoles().removeIf(ur -> ur.getRole().getRole().equals(permission));
        }

        user = userRepository.save(user);
        return convertToResponse(user);
    }

    @Override
    public boolean hasPermission(Long userId, Permission permission) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        return user.getUserRoles().stream()
                .anyMatch(ur -> ur.getRole().getRole().equals(permission));
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(userDetails);

        User user = userDetails.getUser();
        Set<String> permissions = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getRole().getPermission())
                .collect(Collectors.toSet());

        return LoginResponse.builder()
                .token(jwt)
                .type("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .permissions(permissions)
                .build();
    }

    private UserResponse convertToResponse(User user) {
        Set<String> permissions = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getRole().getPermission())
                .collect(Collectors.toSet());

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .permissions(permissions)
                .build();
    }

    private void checkAdminPermission() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User currentUser = userDetails.getUser();

        // Check if user is the default admin
        if (currentUser.getId().equals(1L)) {
            return; // Admin user
        }

        throw new UnauthorizedException("Only admin can perform this action");
    }

    private void checkUpdatePermission(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User currentUser = userDetails.getUser();

        // User can update themselves or admin can update anyone
        if (!currentUser.getId().equals(userId) && !currentUser.getId().equals(1L)) {
            throw new UnauthorizedException("You can only update your own profile");
        }
    }
}
