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
        System.out.println(">>> Étape 1 : Persistance des permissions...");
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
        System.out.println(">>> Étape 2 : Configuration des Rôles (Accès Total pour Manager)...");
        List<Permission> allPerms = permissionRepository.findAll();

        createRoleWithPermissions("ADMIN", "Super Administrateur", allPerms);
        createRoleWithPermissions("MANAGER", "Gestionnaire avec accès complet", allPerms);


        List<Permission> livreurPerms = allPerms.stream()
                .filter(p -> p.getName().equals("COLIS_READ_ASSIGNED")
                        || p.getName().equals("COLIS_UPDATE_STATUS")
                        || p.getName().equals("HISTORIQUE_COMMENT"))
                .collect(Collectors.toList());
        createRoleWithPermissions("LIVREUR", "Livreur de Terrain", livreurPerms);

        createRoleIfNotFound("CLIENT", "Client Expéditeur");
    }

    private void createRoleWithPermissions(String name, String desc, List<Permission> perms) {
        Role role = roleRepository.findByName(name).orElseGet(() ->
                roleRepository.save(Role.builder().name(name).description(desc).build())
        );

        role.setPermissions(new HashSet<>(perms));
        roleRepository.save(role);
        System.out.println("Rôle [" + name + "] mis à jour avec " + perms.size() + " permissions.");
    }

    private void createRoleIfNotFound(String name, String desc) {
        if (roleRepository.findByName(name).isEmpty()) {
            roleRepository.save(Role.builder().name(name).description(desc).build());
        }
    }

    private void createDefaultUsers() {
        System.out.println(">>> Étape 3 : Initialisation des comptes utilisateurs...");

        if (!userRepository.existsByUsername("admin")) {
            createAccount("admin", "admin@smartlogi.com", "admin123", "ADMIN");
        }

        if (!userRepository.existsByUsername("manager")) {
            createAccount("manager", "manager@smartlogi.com", "manager123", "MANAGER");
        }

        if (!userRepository.existsByUsername("client")) {
            createAccount("client", "client@smartlogi.com", "client123", "CLIENT");
        }
    }

    private void createAccount(String username, String email, String password, String roleName) {
        Role role = roleRepository.findByName(roleName).orElseThrow();
        userRepository.save(User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build());
        System.out.println("Compte créé : " + username + " [" + roleName + "]");
    }

    private void syncGlobalPermissions() {
        List<Permission> allPermissions = permissionRepository.findAll();
        Set<Permission> permsSet = new HashSet<>(allPermissions);

        userRepository.findAll().forEach(user -> {
            String rName = user.getRole().getName();
            if ("ADMIN".equals(rName) || "MANAGER".equals(rName)) {
                user.setPermissions(permsSet);
                userRepository.save(user);
            }
        });
        System.out.println(">>> Étape 4 : Synchronisation globale terminée (Admin & Manager).");
    }
}