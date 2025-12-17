package org.example.smartspring.security.config;

import org.example.smartspring.security.entities.User;
import org.example.smartspring.security.enums.Role;
import org.example.smartspring.security.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("!test")
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {

                // Créer un utilisateur ADMIN par défaut
                User admin = User.builder()
                        .username("admin")
                        .email("admin@smartlogi.com")
                        .password(passwordEncoder.encode("admin"))
                        .role(Role.ADMIN)
                        .isActive(true)
                        .build();

                userRepository.save(admin);

                // Créer un utilisateur MANAGER par défaut
                User manager = User.builder()
                        .username("manager")
                        .email("manager@smartlogi.com")
                        .password(passwordEncoder.encode("manager"))
                        .role(Role.MANAGER)
                        .isActive(true)
                        .build();

                userRepository.save(manager);

                // Créer un utilisateur LIVREUR par défaut
                User livreur = User.builder()
                        .username("livreur")
                        .email("livreur@smartlogi.com")
                        .password(passwordEncoder.encode("livreur"))
                        .role(Role.LIVREUR)
                        .isActive(true)
                        .build();

                userRepository.save(livreur);

                // Créer un utilisateur CLIENT par défaut
                User client = User.builder()
                        .username("client")
                        .email("client@smartlogi.com")
                        .password(passwordEncoder.encode("client"))
                        .role(Role.CLIENT)
                        .isActive(true)
                        .build();

                userRepository.save(client);

                // Créer un utilisateur USER par défaut
                User user = User.builder()
                        .username("client")
                        .email("client@smartlogi.com")
                        .password(passwordEncoder.encode("client"))
                        .role(Role.USER)
                        .isActive(true)
                        .build();

                userRepository.save(user);

                System.out.println("========================================");
                System.out.println("Utilisateurs par défaut créés:");
                System.out.println("Admin - username: admin, password: admin123");
                System.out.println("Manager - username: manager, password: manager123");
                System.out.println("Livreur - username: livreur, password: livreur123");
                System.out.println("Client - username: client, password: client123");
                System.out.println("User - username: user, password: user123");
                System.out.println("========================================");
            }
        };
    }
}
