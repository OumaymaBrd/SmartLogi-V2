package org.example.smartspring.security.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.example.smartspring.security.dto.*;
import org.example.smartspring.security.entities.User;
import org.example.smartspring.security.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public AuthResponse refreshToken(String username) {
        entityManager.flush();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (user.getRole() != null) {
            entityManager.detach(user.getRole());
        }
        entityManager.detach(user);
        User freshUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Erreur de synchronisation"));

        if (freshUser.getRole() != null) {
            freshUser.getRole().getPermissions().size();
        }

        return mapToResponse(freshUser, "Permissions synchronisées avec succès");
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        entityManager.clear();

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return mapToResponse(user, "Connexion réussie");
    }

    private AuthResponse mapToResponse(User user, String message) {
        String jwtToken = jwtService.generateToken(user);

        List<String> perms = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> !auth.startsWith("ROLE_"))
                .collect(Collectors.toList());

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