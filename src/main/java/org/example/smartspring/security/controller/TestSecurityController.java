package org.example.smartspring.security.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "*")
public class TestSecurityController {

    @GetMapping("/public")
    public Map<String, String> publicEndpoint() {
        return Map.of("message", "Ceci est un endpoint public - accessible à tous");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> adminEndpoint() {
        return Map.of("message", "Bienvenue ADMIN - vous avez accès à cette ressource protégée");
    }

    @GetMapping("/manager")
    @PreAuthorize("hasRole('MANAGER')")
    public Map<String, String> managerEndpoint() {
        return Map.of("message", "Bienvenue MANAGER - vous avez accès à cette ressource protégée");
    }

    @GetMapping("/livreur")
    @PreAuthorize("hasRole('LIVREUR')")
    public Map<String, String> livreurEndpoint() {
        return Map.of("message", "Bienvenue LIVREUR - vous avez accès à cette ressource protégée");
    }

    @GetMapping("/client")
    @PreAuthorize("hasRole('CLIENT')")
    public Map<String, String> clientEndpoint() {
        return Map.of("message", "Bienvenue CLIENT - vous avez accès à cette ressource protégée");
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public Map<String, String> userEndpoint() {
        return Map.of("message", "Bienvenue USER - vous avez accès à cette ressource protégée");
    }

    @GetMapping("/authenticated")
    @PreAuthorize("isAuthenticated()")
    public Map<String, String> authenticatedEndpoint() {
        return Map.of("message", "Vous êtes authentifié - accessible à tous les utilisateurs connectés");
    }
}
