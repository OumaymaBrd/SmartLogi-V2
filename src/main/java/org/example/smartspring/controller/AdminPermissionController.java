package org.example.smartspring.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartspring.dto.AssignPermissionRequest;
import org.example.smartspring.dto.CreatePermissionRequest;
import org.example.smartspring.dto.PermissionDTO;
import org.example.smartspring.dto.RolePermissionDTO;
import org.example.smartspring.service.PermissionService;
import org.example.smartspring.service.RolePermissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/permissions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminPermissionController {

    private final PermissionService permissionService;
    private final RolePermissionService rolePermissionService;

    // Initialiser les permissions par défaut (à appeler une seule fois)
    @PostMapping("/initialize")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE_PERMISSIONS')")
    public ResponseEntity<String> initializePermissions() {
        permissionService.initializeDefaultPermissions();
        return ResponseEntity.ok("Permissions par défaut initialisées avec succès");
    }

    // Créer une nouvelle permission personnalisée
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN_MANAGE_PERMISSIONS')")
    public ResponseEntity<PermissionDTO> createPermission(@Valid @RequestBody CreatePermissionRequest request) {
        PermissionDTO created = permissionService.createPermission(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Lister toutes les permissions
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN_MANAGE_PERMISSIONS')")
    public ResponseEntity<List<PermissionDTO>> getAllPermissions() {
        List<PermissionDTO> permissions = permissionService.getAllPermissions();
        return ResponseEntity.ok(permissions);
    }

    // Lister les permissions par catégorie
    @GetMapping("/category/{category}")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE_PERMISSIONS')")
    public ResponseEntity<List<PermissionDTO>> getPermissionsByCategory(@PathVariable String category) {
        List<PermissionDTO> permissions = permissionService.getPermissionsByCategory(category);
        return ResponseEntity.ok(permissions);
    }

    // Supprimer une permission
    @DeleteMapping("/{permissionId}")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE_PERMISSIONS')")
    public ResponseEntity<Void> deletePermission(@PathVariable String permissionId) {
        permissionService.deletePermission(permissionId);
        return ResponseEntity.noContent().build();
    }

    // Assigner des permissions à un rôle
    @PostMapping("/roles/{roleId}/assign")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE_ROLES')")
    public ResponseEntity<RolePermissionDTO> assignPermissionsToRole(
            @PathVariable String roleId,
            @Valid @RequestBody AssignPermissionRequest request
    ) {
        RolePermissionDTO result = rolePermissionService.assignPermissionsToRole(roleId, request);
        return ResponseEntity.ok(result);
    }

    // Retirer une permission d'un rôle
    @DeleteMapping("/roles/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE_ROLES')")
    public ResponseEntity<RolePermissionDTO> removePermissionFromRole(
            @PathVariable String roleId,
            @PathVariable String permissionId
    ) {
        RolePermissionDTO result = rolePermissionService.removePermissionFromRole(roleId, permissionId);
        return ResponseEntity.ok(result);
    }

    // Consulter les permissions d'un rôle
    @GetMapping("/roles/{roleId}")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE_ROLES')")
    public ResponseEntity<RolePermissionDTO> getRolePermissions(@PathVariable String roleId) {
        RolePermissionDTO result = rolePermissionService.getRoleWithPermissions(roleId);
        return ResponseEntity.ok(result);
    }
}
