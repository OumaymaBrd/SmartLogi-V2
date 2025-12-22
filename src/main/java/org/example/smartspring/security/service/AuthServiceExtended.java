package org.example.smartspring.security.service;

import lombok.RequiredArgsConstructor;
import org.example.smartspring.security.dto.AuthResponse;
import org.example.smartspring.security.dto.LoginRequest;
import org.example.smartspring.security.dto.RegisterRequest;
import org.example.smartspring.entities.Permission;
import org.example.smartspring.security.entities.User;
import org.example.smartspring.security.entities.Role; // Utilisation de l'ENTITÉ
import org.example.smartspring.security.repository.UserRepository;
import org.example.smartspring.security.repository.RoleRepository; // Obligatoire
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
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtServiceExtended jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Le nom d'utilisateur existe déjà");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("L'email existe déjà");
        }

        String roleToFind = (request.getRole() != null) ? request.getRole().toString() : "CLIENT";
        Role userRole = roleRepository.findByName(roleToFind)
                .orElseThrow(() -> new RuntimeException("Rôle " + roleToFind + " non trouvé en base"));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .permissions(new HashSet<>())
                .build();

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);

        return mapToResponse(savedUser, token, "Inscription réussie");
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        String token = jwtService.generateToken(user);

        return mapToResponse(user, token, "Connexion réussie");
    }

    private AuthResponse mapToResponse(User user, String token, String message) {
        List<String> permissionNames = user.getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toList());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole().getName() : "NONE")
                .permissions(permissionNames)
                .message(message)
                .build();
    }
}