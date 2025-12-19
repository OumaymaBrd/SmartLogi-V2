package org.example.smartspring.security.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smartspring.dto.AssignPermissionRequest;
import org.example.smartspring.dto.PermissionDTO;
import org.example.smartspring.dto.RolePermissionDTO;
import org.example.smartspring.entities.Permission;
import org.example.smartspring.repository.PermissionRepository;
import org.example.smartspring.security.entities.Role;
import org.example.smartspring.security.repository.RoleRepository;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RolePermissionService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public RolePermissionDTO assignPermissionsToRole(String roleId, AssignPermissionRequest request) {
        Role role = roleRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new RuntimeException("Rôle non trouvé"));

        Set<Permission> permissions = new HashSet<>(
                permissionRepository.findAllById(request.getPermissionIds())
        );

        if (permissions.isEmpty()) {
            throw new RuntimeException("Aucune permission valide trouvée");
        }

        role.getPermissions().addAll(permissions);
        Role savedRole = roleRepository.save(role);

        entityManager.flush();
        entityManager.clear();

        log.info("✅ Assigned {} permissions to role {}, total: {} - Cache cleared!",
                permissions.size(),
                role.getName(),
                savedRole.getPermissions().size());

        return mapToDTO(savedRole);
    }

    @Transactional
    public RolePermissionDTO removePermissionFromRole(String roleId, String permissionId) {
        Role role = roleRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new RuntimeException("Rôle non trouvé"));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission non trouvée"));

        role.getPermissions().remove(permission);
        Role savedRole = roleRepository.save(role);

        entityManager.flush();
        entityManager.clear();

        log.info("✅ Removed permission {} from role {}, remaining: {} - Cache cleared!",
                permission.getName(),
                role.getName(),
                savedRole.getPermissions().size());

        return mapToDTO(savedRole);
    }

    @Transactional(readOnly = true)
    public RolePermissionDTO getRoleWithPermissions(String roleId) {
        Role role = roleRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new RuntimeException("Rôle non trouvé"));
        return mapToDTO(role);
    }

    private RolePermissionDTO mapToDTO(Role role) {
        List<PermissionDTO> permissionDTOs = role.getPermissions().stream()
                .map(p -> PermissionDTO.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .description(p.getDescription())
                        .category(p.getCategory())
                        .build())
                .collect(Collectors.toList());

        return RolePermissionDTO.builder()
                .roleId(role.getId())
                .roleName(role.getName())
                .roleDescription(role.getDescription())
                .permissions(permissionDTOs)
                .totalPermissions(permissionDTOs.size())
                .build();
    }
}
