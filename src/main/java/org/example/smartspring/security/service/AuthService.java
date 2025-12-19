package org.example.smartspring.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smartspring.security.dto.*;
import org.example.smartspring.security.entities.User;
import org.example.smartspring.security.entities.Role;
import org.example.smartspring.security.repository.UserRepository;
import org.example.smartspring.security.repository.RoleRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        Role userRole = roleRepository.findByName(request.getRole() != null ? request.getRole().toString() : "USER")
                .orElseThrow(() -> new RuntimeException("Rôle non trouvé"));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .build();

        userRepository.save(user);
        return mapToResponse(user, "Inscription réussie");
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // Recharger l'utilisateur avec TOUTES les permissions à jour depuis la DB
        User user = userRepository.findByUsernameWithPermissions(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        log.info("User {} logged in with {} authorities",
                user.getUsername(),
                user.getAuthorities().size());

        return mapToResponse(user, "Connexion réussie");
    }

    private AuthResponse mapToResponse(User user, String message) {
        String jwtToken = jwtService.generateToken(user);
        List<String> perms = user.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .filter(auth -> !auth.startsWith("ROLE_"))
                .collect(Collectors.toList());

        log.info("Token generated with {} permissions for user {}", perms.size(), user.getUsername());

        return AuthResponse.builder()
                .token(jwtToken)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole().getName() : "USER")
                .permissions(perms)
                .message(message)
                .build();
    }
}
