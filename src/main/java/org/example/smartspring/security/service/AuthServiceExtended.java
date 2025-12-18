package org.example.smartspring.security.service;

import lombok.RequiredArgsConstructor;
import org.example.smartspring.security.dto.AuthResponse;
import org.example.smartspring.security.dto.LoginRequest;
import org.example.smartspring.security.dto.RegisterRequest;
import org.example.smartspring.entities.Permission;
import org.example.smartspring.security.entities.User;
import org.example.smartspring.security.enums.Role;
import org.example.smartspring.security.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceExtended {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtServiceExtended jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Vérifier si l'utilisateur existe déjà
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Le nom d'utilisateur existe déjà");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("L'email existe déjà");
        }

        // Créer l'utilisateur
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.CLIENT)
                .permissions(new HashSet<>())
                .build();

        User savedUser = userRepository.save(user);

        // Générer le token
        String token = jwtService.generateToken(savedUser);

        return AuthResponse.builder()
                .token(token)
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .permissions(savedUser.getPermissions().stream()
                        .map(Permission::getName)
                        .collect(Collectors.toList()))
                .message("Inscription réussie")
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // Authentifier l'utilisateur
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Récupérer l'utilisateur
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Générer le token
        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .permissions(user.getPermissions().stream()
                        .map(Permission::getName)
                        .collect(Collectors.toList()))
                .message("Connexion réussie")
                .build();
    }
}
