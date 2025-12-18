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
            // On ne lance l'initialisation que si la base est vide
            if (userRepository.count() == 0) {

                // 1. ADMIN
                userRepository.save(User.builder()
                        .username("admin")
                        .email("admin@smartlogi.com")
                        .password(passwordEncoder.encode("admin123"))
                        .role(Role.ADMIN)
                        .isActive(true)
                        .build());

                // 2. MANAGER
                userRepository.save(User.builder()
                        .username("manager")
                        .email("manager@smartlogi.com")
                        .password(passwordEncoder.encode("manager123"))
                        .role(Role.MANAGER)
                        .isActive(true)
                        .build());

                // 3. LIVREUR
                userRepository.save(User.builder()
                        .username("livreur")
                        .email("livreur@smartlogi.com")
                        .password(passwordEncoder.encode("livreur123"))
                        .role(Role.LIVREUR)
                        .isActive(true)
                        .build());

                // 4. CLIENT
                userRepository.save(User.builder()
                        .username("client")
                        .email("client@smartlogi.com")
                        .password(passwordEncoder.encode("client123"))
                        .role(Role.CLIENT)
                        .isActive(true)
                        .build());

                // 5. USER (C'est ici qu'était l'erreur de doublon)
                userRepository.save(User.builder()
                        .username("user")
                        .email("user@smartlogi.com")
                        .password(passwordEncoder.encode("user123"))
                        .role(Role.USER)
                        .isActive(true)
                        .build());

                System.out.println("========================================");
                System.out.println("Succès : Utilisateurs par défaut créés !");
                System.out.println("Admin    -> admin / admin123");
                System.out.println("Manager  -> manager / manager123");
                System.out.println("Livreur  -> livreur / livreur123");
                System.out.println("Client   -> client / client123");
                System.out.println("User     -> user / user123");
                System.out.println("========================================");
            } else {
                System.out.println("DataInitializer : Des utilisateurs existent déjà, saut de l'initialisation.");
            }
        };
    }
}