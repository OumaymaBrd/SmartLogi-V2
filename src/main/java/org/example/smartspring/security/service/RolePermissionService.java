package org.example.smartspring.service;

import lombok.RequiredArgsConstructor;
import org.example.smartspring.dto.AssignPermissionRequest;
import org.example.smartspring.dto.PermissionDTO;
import org.example.smartspring.dto.RolePermissionDTO;
import org.example.smartspring.entities.Permission;
import org.example.smartspring.exception.ResourceNotFoundException;
import org.example.smartspring.repository.PermissionRepository;
import org.example.smartspring.security.entities.Role;
import org.example.smartspring.security.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolePermissionService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Transactional
    public RolePermissionDTO assignPermissionsToRole(String roleId, AssignPermissionRequest request) {
        Role role = roleRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Rôle non trouvé avec l'ID: " + roleId));

        Set<Permission> permissions = request.getPermissionIds().stream()
                .map(permId -> permissionRepository.findById(permId)
                        .orElseThrow(() -> new ResourceNotFoundException("Permission non trouvée avec l'ID: " + permId)))
                .collect(Collectors.toSet());

        // Clear and add all to ensure type compatibility
        permissions.forEach(role.getPermissions()::add);

        Role updated = roleRepository.save(role);

        return mapToRolePermissionDTO(updated);
    }

    @Transactional
    public RolePermissionDTO removePermissionFromRole(String roleId, String permissionId) {
        Role role = roleRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Rôle non trouvé avec l'ID: " + roleId));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission non trouvée avec l'ID: " + permissionId));

        role.getPermissions().remove(permission);
        Role updated = roleRepository.save(role);

        return mapToRolePermissionDTO(updated);
    }

    public RolePermissionDTO getRoleWithPermissions(String roleId) {
        Role role = roleRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Rôle non trouvé avec l'ID: " + roleId));
        return mapToRolePermissionDTO(role);
    }

    private RolePermissionDTO mapToRolePermissionDTO(Role role) {
        Set<PermissionDTO> permissionDTOs = role.getPermissions().stream()
                .map(perm -> PermissionDTO.builder()
                        .id(perm.getId())
                        .name(perm.getName())
                        .description(perm.getDescription())
                        .category(perm.getCategory())
                        .createdAt(perm.getCreatedAt())
                        .build())
                .collect(Collectors.toSet());

        return RolePermissionDTO.builder()
                .roleId(role.getId())
                .roleName(role.getName())
                .roleDescription(role.getDescription())
                .permissions(permissionDTOs)
                .build();
    }
}
