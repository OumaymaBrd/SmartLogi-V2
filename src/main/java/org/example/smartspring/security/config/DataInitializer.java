package org.example.smartspring.security.config;

import lombok.RequiredArgsConstructor;
import org.example.smartspring.security.entities.Role;
import org.example.smartspring.security.entities.User;
import org.example.smartspring.security.repository.RoleRepository;
import org.example.smartspring.security.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

//@Configuration
@Profile("!test")
public class DataInitializer {

    @Bean
    @Transactional
    public CommandLineRunner initData(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {

                // 1. Créer ou récupérer les ENTITÉS Role (indispensable pour la nouvelle DB)
                Role adminRole = getOrCreateRole(roleRepository, "ADMIN", "Administrateur système");
                Role managerRole = getOrCreateRole(roleRepository, "MANAGER", "Gestionnaire logistique");
                Role livreurRole = getOrCreateRole(roleRepository, "LIVREUR", "Livreur de colis");
                Role clientRole = getOrCreateRole(roleRepository, "CLIENT", "Client expéditeur");
                Role userRole = getOrCreateRole(roleRepository, "USER", "Utilisateur standard");

                // 2. Sauvegarder les utilisateurs avec les objets Role créés
                createUser(userRepository, passwordEncoder, "admin", "admin@smartlogi.com", "admin123", adminRole);
                createUser(userRepository, passwordEncoder, "manager", "manager@smartlogi.com", "manager123", managerRole);
                createUser(userRepository, passwordEncoder, "livreur", "livreur@smartlogi.com", "livreur123", livreurRole);
                createUser(userRepository, passwordEncoder, "client", "client@smartlogi.com", "client123", clientRole);
                createUser(userRepository, passwordEncoder, "user", "user@smartlogi.com", "user123", userRole);

                System.out.println("========================================");
                System.out.println("Succès : Rôles et Utilisateurs par défaut créés !");
                System.out.println("Admin    -> admin / admin123");
                System.out.println("Manager  -> manager / manager123");
                System.out.println("========================================");
            } else {
                System.out.println("DataInitializer : Des données existent déjà, saut.");
            }
        };
    }

    private Role getOrCreateRole(RoleRepository repository, String name, String description) {
        return repository.findByName(name)
                .orElseGet(() -> repository.save(Role.builder()
                        .name(name)
                        .description(description)
                        .build()));
    }

    private void createUser(UserRepository repo, PasswordEncoder encoder, String username, String email, String pass, Role role) {
        repo.save(User.builder()
                .username(username)
                .email(email)
                .password(encoder.encode(pass))
                .role(role) // On passe l'objet Role ici
                .build());
    }
}