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
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public OAuth2LoginSuccessHandler(JwtService jwtService, UserRepository userRepository,
                                     RoleRepository roleRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2AuthenticationToken authContext = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String regId = authContext.getAuthorizedClientRegistrationId();

        String email = oAuth2User.getAttribute("email");
        if (email == null) email = oAuth2User.getAttribute("login");
        if (email == null) email = oAuth2User.getName();

        String providerId = oAuth2User.getAttribute("sub");
        if (providerId == null) providerId = oAuth2User.getName();

        AuthProvider provider = AuthProvider.LOCAL;
        if ("google".equals(regId)) provider = AuthProvider.GOOGLE;
        else if ("okta".equals(regId)) {
            String subLower = providerId.toLowerCase();
            if (subLower.contains("github")) provider = AuthProvider.GITHUB;
            else if (subLower.contains("facebook")) provider = AuthProvider.FACEBOOK;
            else provider = AuthProvider.OKTA;
        }

        final AuthProvider finalProvider = provider;
        final String finalEmail = email;
        final String finalId = providerId;

        User user = userRepository.findByEmail(finalEmail)
                .orElseGet(() -> userRepository.findByProviderId(finalId)
                        .orElseGet(() -> {
                            Role role = roleRepository.findByName("CLIENT").orElseThrow();
                            return User.builder()
                                    .username(finalEmail)
                                    .email(finalEmail)
                                    .enabled(true)
                                    .role(role)
                                    .build();
                        }));

        user.setPassword(passwordEncoder.encode("1234567"));
        user.setProvider(finalProvider);
        user.setProviderId(finalId);
        user.setEnabled(true);
        userRepository.save(user);

        String token = jwtService.generateToken(user);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format(
                "{\"status\": \"SUCCESS\", \"token\": \"%s\", \"username\": \"%s\"}",
                token, user.getUsername()));
    }
}