package org.example.smartspring.security.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.smartspring.security.entities.Role;
import org.example.smartspring.security.entities.User;
import org.example.smartspring.security.enums.AuthProvider;
import org.example.smartspring.security.repository.RoleRepository;
import org.example.smartspring.security.repository.UserRepository;
import org.example.smartspring.security.service.JwtService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public OAuth2LoginSuccessHandler(JwtService jwtService,
                                     UserRepository userRepository,
                                     RoleRepository roleRepository,
                                     @Lazy PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String googleId = oAuth2User.getAttribute("sub");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            Role clientRole = roleRepository.findByName("CLIENT")
                    .orElseThrow(() -> new RuntimeException("Rôle CLIENT non trouvé en base."));

            return userRepository.save(User.builder()
                    .email(email)
                    .username(email)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .role(clientRole)
                    .provider(AuthProvider.GOOGLE)
                    .providerId(googleId)
                    .enabled(true)
                    .build());
        });

        if (user.getProviderId() == null) {
            user.setProvider(AuthProvider.GOOGLE);
            user.setProviderId(googleId);
            userRepository.save(user);
        }

        String token = jwtService.generateToken(user);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                String.format("{\"status\": \"SUCCESS\", \"provider\": \"%s\", \"token\": \"%s\"}", user.getProvider(), token)
        );
    }
}