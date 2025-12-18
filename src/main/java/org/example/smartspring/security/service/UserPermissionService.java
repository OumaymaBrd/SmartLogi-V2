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

    @Transactional
    public void assignPermissionToUser(Long userId, String permissionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission non trouvée avec l'ID: " + permissionId));

        user.getPermissions().add(permission);
        userRepository.save(user);
    }

    @Transactional
    public void removePermissionFromUser(Long userId, String permissionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission non trouvée avec l'ID: " + permissionId));

        user.getPermissions().remove(permission);
        userRepository.save(user);
    }

    public UserPermissionsDTO getUserPermissions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId));

        List<String> permissions = user.getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toList());

        return UserPermissionsDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .permissions(permissions)
                .build();
    }

    @Transactional
    public void assignPermissionsByName(Long userId, List<String> permissionNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId));

        for (String permissionName : permissionNames) {
            Permission permission = permissionRepository.findByName(permissionName)
                    .orElseThrow(() -> new RuntimeException("Permission non trouvée: " + permissionName));
            user.getPermissions().add(permission);
        }

        userRepository.save(user);
    }
}
