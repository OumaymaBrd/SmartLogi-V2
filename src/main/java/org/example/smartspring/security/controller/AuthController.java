package org.example.smartspring.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartspring.security.dto.AuthResponse;
import org.example.smartspring.security.dto.LoginRequest;
import org.example.smartspring.security.dto.RegisterRequest;
import org.example.smartspring.security.service.AuthService;
import org.example.smartspring.security.service.AuthServiceExtended;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authServices;

    private final AuthServiceExtended authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    AuthResponse.builder()
                            .message(e.getMessage())
                            .build()
            );
        }
    }

//    @PostMapping("/login")
//    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
//        try {
//            AuthResponse response = authService.login(request);
//            return ResponseEntity.ok(response);
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(
//                    AuthResponse.builder()
//                            .message("Nom d'utilisateur ou mot de passe incorrect")
//                            .build()
//            );
//        }
//    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());

        return ResponseEntity.ok(Map.of("message", "Déconnexion réussie, session libérée"));
    }

    @GetMapping("/refresh-my-permissions")
    public ResponseEntity<AuthResponse> refreshPermissions(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(authServices.refreshToken(principal.getName()));
    }
}
