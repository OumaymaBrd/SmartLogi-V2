package org.example.smartspring.security.config;

import lombok.RequiredArgsConstructor;
import org.example.smartspring.entities.Permission;
import org.example.smartspring.security.entities.Role;
import org.example.smartspring.security.entities.User;
import org.example.smartspring.security.enums.PermissionEnum;
import org.example.smartspring.repository.PermissionRepository;
import org.example.smartspring.security.repository.UserRepository;
import org.example.smartspring.security.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PermissionInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        initializePermissions();
        initializeRoles();
        createDefaultUsers();
        syncGlobalPermissions();
    }

    private void initializePermissions() {
        for (PermissionEnum permEnum : PermissionEnum.values()) {
            if (!permissionRepository.existsByName(permEnum.name())) {
                permissionRepository.save(Permission.builder()
                        .name(permEnum.name())
                        .description(permEnum.getDescription())
                        .category(permEnum.getCategory())
                        .build());
            }
        }
    }

    private void initializeRoles() {
        List<Permission> allPerms = permissionRepository.findAll();
        createRoleWithPermissions("ADMIN", "Super Administrateur", allPerms);
        createRoleWithPermissions("MANAGER", "Gestionnaire complet", allPerms);

        List<Permission> livreurPerms = allPerms.stream()
                .filter(p -> p.getName().contains("COLIS_READ") || p.getName().contains("UPDATE_STATUS"))
                .collect(Collectors.toList());
        createRoleWithPermissions("LIVREUR", "Livreur", livreurPerms);

        if (roleRepository.findByName("CLIENT").isEmpty()) {
            roleRepository.save(Role.builder().name("CLIENT").description("Client").build());
        }
    }

    private void createRoleWithPermissions(String name, String desc, List<Permission> perms) {
        Role role = roleRepository.findByName(name).orElseGet(() ->
                roleRepository.save(Role.builder().name(name).description(desc).build())
        );
        role.setPermissions(new HashSet<>(perms));
        roleRepository.save(role);
    }

    private void createDefaultUsers() {
        if (!userRepository.existsByUsername("admin")) {
            createAccount("admin", "admin@smartlogi.com", "admin123", "ADMIN");
        }
        if (!userRepository.existsByUsername("manager")) {
            createAccount("manager", "manager@smartlogi.com", "manager123", "MANAGER");
        }
    }

    private void createAccount(String username, String email, String password, String roleName) {
        Role role = roleRepository.findByName(roleName).orElseThrow();
        userRepository.save(User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .enabled(true)
                .build());
    }

    private void syncGlobalPermissions() {
        List<Permission> allPermissions = permissionRepository.findAll();
        Set<Permission> permsSet = new HashSet<>(allPermissions);
        userRepository.findAll().forEach(user -> {
            if (user.getRole() != null && ("ADMIN".equals(user.getRole().getName()) || "MANAGER".equals(user.getRole().getName()))) {
                user.setPermissions(permsSet);
                userRepository.save(user);
            }
        });
    }
}