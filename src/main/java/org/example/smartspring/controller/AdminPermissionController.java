package org.example.smartspring.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smartspring.dto.AssignPermissionRequest;
import org.example.smartspring.dto.CreatePermissionRequest;
import org.example.smartspring.dto.PermissionDTO;
import org.example.smartspring.dto.RolePermissionDTO;
import org.example.smartspring.service.PermissionService;
import org.example.smartspring.security.service.RolePermissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/permissions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class AdminPermissionController {

    private final PermissionService permissionService;
    private final RolePermissionService rolePermissionService;

    @PostMapping("/initialize")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE_PERMISSIONS')")
    public ResponseEntity<String> initializePermissions() {
        logAuthenticationDetails();
        permissionService.initializeDefaultPermissions();
        return ResponseEntity.ok("Permissions par défaut initialisées avec succès");
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN_MANAGE_PERMISSIONS')")
    public ResponseEntity<PermissionDTO> createPermission(@Valid @RequestBody CreatePermissionRequest request) {
        logAuthenticationDetails();
        PermissionDTO created = permissionService.createPermission(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN_MANAGE_PERMISSIONS')")
    public ResponseEntity<List<PermissionDTO>> getAllPermissions() {
        logAuthenticationDetails();
        List<PermissionDTO> permissions = permissionService.getAllPermissions();
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/category/{category}")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE_PERMISSIONS')")
    public ResponseEntity<List<PermissionDTO>> getPermissionsByCategory(@PathVariable String category) {
        logAuthenticationDetails();
        List<PermissionDTO> permissions = permissionService.getPermissionsByCategory(category);
        return ResponseEntity.ok(permissions);
    }

    @DeleteMapping("/{permissionId}")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE_PERMISSIONS')")
    public ResponseEntity<Void> deletePermission(@PathVariable String permissionId) {
        logAuthenticationDetails();
        permissionService.deletePermission(permissionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/roles/{roleId}/assign")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE_ROLES')")
    public ResponseEntity<RolePermissionDTO> assignPermissionsToRole(
            @PathVariable String roleId,
            @Valid @RequestBody AssignPermissionRequest request
    ) {
        logAuthenticationDetails();
        RolePermissionDTO result = rolePermissionService.assignPermissionsToRole(roleId, request);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/roles/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE_ROLES')")
    public ResponseEntity<RolePermissionDTO> removePermissionFromRole(
            @PathVariable String roleId,
            @PathVariable String permissionId
    ) {
        logAuthenticationDetails();
        RolePermissionDTO result = rolePermissionService.removePermissionFromRole(roleId, permissionId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/roles/{roleId}")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE_ROLES')")
    public ResponseEntity<RolePermissionDTO> getRolePermissions(@PathVariable String roleId) {
        logAuthenticationDetails();
        RolePermissionDTO result = rolePermissionService.getRoleWithPermissions(roleId);
        return ResponseEntity.ok(result);
    }

    private void logAuthenticationDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            log.debug("Authenticated user: {}", auth.getName());
            log.debug("Authorities: {}", auth.getAuthorities());
            log.debug("Is authenticated: {}", auth.isAuthenticated());
        } else {
            log.warn("No authentication found in SecurityContext");
        }
    }
}
