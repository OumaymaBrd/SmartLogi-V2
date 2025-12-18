package org.example.smartspring.security.config;

import lombok.RequiredArgsConstructor;
import org.example.smartspring.entities.Permission;
import org.example.smartspring.security.entities.User;
import org.example.smartspring.security.enums.PermissionEnum;
import org.example.smartspring.security.enums.Role;
import org.example.smartspring.repository.PermissionRepository;
import org.example.smartspring.security.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PermissionInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional // Nécessaire pour manipuler les collections JPA (permissions)
    public void run(String... args) {
        initializePermissions();
        createDefaultUsers();
        syncAdminPermissions();
    }

    private void initializePermissions() {
        System.out.println(">>> Initialisation des permissions en base...");
        for (PermissionEnum permEnum : PermissionEnum.values()) {
            if (!permissionRepository.existsByName(permEnum.name())) {
                Permission permission = Permission.builder()
                        .name(permEnum.name())
                        .description(permEnum.getDescription())
                        .category(permEnum.getCategory())
                        .build();
                permissionRepository.save(permission);
                System.out.println("Permission créée: " + permEnum.name());
            }
        }
    }

    private void createDefaultUsers() {
        System.out.println(">>> Vérification des utilisateurs par défaut...");

        // ADMIN
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@smartlogi.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .permissions(new HashSet<>()) // Sera rempli par syncAdminPermissions
                    .build();
            userRepository.save(admin);
            System.out.println("Utilisateur ADMIN créé.");
        }

        // GESTIONNAIRE
        if (!userRepository.existsByUsername("gestionnaire")) {
            createUserWithPermissions("gestionnaire", "gestionnaire@smartlogi.com", "gestionnaire123", Role.MANAGER,
                    "COLIS_READ_ALL", "COLIS_CREATE", "LIVREUR_READ", "ZONE_READ");
        }

        // LIVREUR
        if (!userRepository.existsByUsername("livreur")) {
            createUserWithPermissions("livreur", "livreur@smartlogi.com", "livreur123", Role.LIVREUR,
                    "COLIS_READ_ASSIGNED", "COLIS_UPDATE_STATUS");
        }
    }

    /**
     * Garantit que l'administrateur possède toujours 100% des permissions définies dans l'Enum
     */
    private void syncAdminPermissions() {
        userRepository.findByUsername("admin").ifPresent(admin -> {
            List<Permission> allPermissions = permissionRepository.findAll();
            admin.setPermissions(new HashSet<>(allPermissions));
            userRepository.save(admin);
            System.out.println(">>> Synchronisation : Admin possède désormais les " + allPermissions.size() + " permissions.");
        });
    }

    private void createUserWithPermissions(String username, String email, String password, Role role, String... perms) {
        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .permissions(getPermissionsByNames(perms))
                .build();
        userRepository.save(user);
        System.out.println("Utilisateur " + username + " créé.");
    }

    private Set<Permission> getPermissionsByNames(String... names) {
        Set<Permission> permissions = new HashSet<>();
        for (String name : names) {
            permissionRepository.findByName(name).ifPresent(permissions::add);
        }
        return permissions;
    }
}