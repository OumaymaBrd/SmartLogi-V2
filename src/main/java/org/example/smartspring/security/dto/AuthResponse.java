package org.example.smartspring.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor

public class AuthResponse {
    private String token;
    private Long userId;
    private String username;
    private String email;
    private String role;
    private List<String> permissions;
    private String message;
}
