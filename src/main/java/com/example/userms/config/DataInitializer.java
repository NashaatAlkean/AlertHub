package com.example.userms.config;

import com.example.userms.entity.Role;
import com.example.userms.entity.User;
import com.example.userms.entity.UserRole;
import com.example.userms.enums.Permission;
import com.example.userms.repository.RoleRepository;
import com.example.userms.repository.UserRepository;
import com.example.userms.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;  // ADD THIS

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🔄 DataInitializer started...");

        // Initialize roles if they don't exist
        long roleCount = roleRepository.count();
        System.out.println("📊 Current role count: " + roleCount);

        if (roleCount == 0) {
            for (Permission permission : Permission.values()) {
                Role role = Role.builder()
                        .role(permission)
                        .build();
                roleRepository.save(role);
                System.out.println("✅ Created role: " + permission);
            }
            System.out.println("✅ Roles initialized successfully");
        }

        // Create default admin user if it doesn't exist
        boolean adminExists = userRepository.existsByUsername("admin");
        System.out.println("👤 Admin exists: " + adminExists);

        if (!adminExists) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@gmail.com")
                    .phone("0509999999")
                    .password(passwordEncoder.encode("admin1234"))
                    .userRoles(new HashSet<>())
                    .build();

            admin = userRepository.save(admin);
            System.out.println("✅ Admin user created with ID: " + admin.getId());

            // Grant all permissions to admin
            int permissionCount = 0;
            for (Permission permission : Permission.values()) {
                Role role = roleRepository.findByRole(permission)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + permission));

                UserRole userRole = UserRole.builder()
                        .user(admin)
                        .role(role)
                        .build();

                // SAVE UserRole directly using the repository
                userRoleRepository.save(userRole);

                permissionCount++;
                System.out.println("  ➕ Granted: " + permission);
            }

            System.out.println("✅ Admin granted " + permissionCount + " permissions");
        }

        // Create example user Nashaat if it doesn't exist
        boolean nashaatExists = userRepository.existsByUsername("Nashaat");
        System.out.println("👤 Nashaat exists: " + nashaatExists);

        if (!nashaatExists) {
            User nashaat = User.builder()
                    .username("Nashaat")
                    .email("Nashaat@gmail.com")
                    .phone("0505577260")
                    .password(passwordEncoder.encode("secret"))
                    .userRoles(new HashSet<>())
                    .build();

            nashaat = userRepository.save(nashaat);
            System.out.println("✅ Nashaat user created with ID: " + nashaat.getId());

            // Grant specific permissions to Nashaat
            String[] nashaatPermissions = {"createAction", "updateAction", "deleteAction", "triggerScan"};
            for (String permissionStr : nashaatPermissions) {
                Permission permission = Permission.fromString(permissionStr);
                Role role = roleRepository.findByRole(permission)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + permission));

                UserRole userRole = UserRole.builder()
                        .user(nashaat)
                        .role(role)
                        .build();

                // SAVE UserRole directly using the repository
                userRoleRepository.save(userRole);

                System.out.println("  ➕ Granted: " + permission);
            }

            System.out.println("✅ Nashaat granted 4 permissions");
        }

        System.out.println("🎉 DataInitializer completed!");
    }
}