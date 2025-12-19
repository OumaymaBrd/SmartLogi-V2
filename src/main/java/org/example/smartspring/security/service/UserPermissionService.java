package org.example.smartspring.security.service;

import lombok.RequiredArgsConstructor;
import org.example.smartspring.security.dto.UserPermissionsDTO;
import org.example.smartspring.entities.Permission;
import org.example.smartspring.security.entities.User;
import org.example.smartspring.repository.PermissionRepository;
import org.example.smartspring.security.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserPermissionService {
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;

    public UserPermissionsDTO getUserPermissions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        List<String> permissionNames = user.getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toList());

        return UserPermissionsDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                // FIX IMAGE 14 : On utilise .getName() car le champ est privé
                .role(user.getRole() != null ? user.getRole().getName() : "NONE")
                .permissions(permissionNames)
                .build();
    }

    @Transactional
    public void assignPermissionToUser(Long userId, String permissionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Permission perm = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission non trouvée"));

        user.getPermissions().add(perm); // Fonctionne maintenant
        userRepository.save(user);
    }
}