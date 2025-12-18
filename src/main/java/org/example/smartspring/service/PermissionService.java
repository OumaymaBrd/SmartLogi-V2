package org.example.smartspring.service;

import lombok.RequiredArgsConstructor;
import org.example.smartspring.dto.CreatePermissionRequest;
import org.example.smartspring.dto.PermissionDTO;
import org.example.smartspring.entities.Permission;
import org.example.smartspring.security.enums.PermissionEnum;
import org.example.smartspring.exception.ResourceNotFoundException;
import org.example.smartspring.repository.PermissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public List<PermissionDTO> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PermissionDTO> getPermissionsByCategory(String category) {
        return permissionRepository.findByCategory(category).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PermissionDTO createPermission(CreatePermissionRequest request) {
        if (permissionRepository.existsByName(request.getName())) {
            throw new RuntimeException("La permission '" + request.getName() + "' existe déjà");
        }
        Permission permission = Permission.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .build();
        return mapToDTO(permissionRepository.save(permission));
    }

    @Transactional
    public void deletePermission(String id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission non trouvée"));
        permission.getRoles().forEach(role -> role.getPermissions().remove(permission));
        permissionRepository.delete(permission);
    }

    @Transactional
    public void initializeDefaultPermissions() {
        for (PermissionEnum permEnum : PermissionEnum.values()) {
            if (!permissionRepository.existsByName(permEnum.name())) {
                permissionRepository.save(Permission.builder()
                        .name(permEnum.name())
                        .description(permEnum.getDescription())
                        .category(permEnum.getCategory())
                        .build());
            }
        }
    }

    private PermissionDTO mapToDTO(Permission p) {
        return PermissionDTO.builder()
                .id(p.getId()).name(p.getName()).description(p.getDescription())
                .category(p.getCategory()).createdAt(p.getCreatedAt()).build();
    }
}