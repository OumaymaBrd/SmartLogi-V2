package org.example.smartspring.security.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smartspring.dto.AssignPermissionRequest;
import org.example.smartspring.dto.RolePermissionDTO;
import org.example.smartspring.dto.PermissionDTO;
import org.example.smartspring.entities.Permission;
import org.example.smartspring.repository.PermissionRepository;
import org.example.smartspring.security.entities.Role;
import org.example.smartspring.security.repository.RoleRepository;
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

        Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(request.getPermissionIds()));

        role.getPermissions().addAll(permissions);

        // saveAndFlush écrit immédiatement le nouvel état dans role_permissions
        roleRepository.saveAndFlush(role);

        entityManager.clear();
        return getRoleWithPermissions(roleId);
    }

    @Transactional
    public RolePermissionDTO removePermissionFromRole(String roleId, String permissionId) {
        Role role = roleRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new RuntimeException("Rôle non trouvé"));

        // Utilisation de removeIf pour garantir la suppression dans le Set par ID
        boolean removed = role.getPermissions().removeIf(p -> p.getId().equals(permissionId));

        if (!removed) {
            log.warn("Permission non trouvée dans ce rôle : {}", permissionId);
        }

        // Force la suppression physique en base de données
        roleRepository.saveAndFlush(role);

        // On vide la session pour que le prochain appel (ou le refresh token) relise MySQL
        entityManager.flush();
        entityManager.clear();

        log.info("✅ Relation supprimée en base de données pour le rôle {}", role.getName());

        // On recharge une version propre pour le retour DTO
        Role updatedRole = roleRepository.findByIdWithPermissions(roleId).orElseThrow();
        return mapToDTO(updatedRole);
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
                        .category(p.getCategory())
                        .description(p.getDescription())
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

    @Transactional(readOnly = true)
    public List<RolePermissionDTO> getAllRolesWithPermissions() {
        log.info("Récupération de tous les rôles avec leurs permissions");
        List<Role> roles = roleRepository.findAll();

        return roles.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


}