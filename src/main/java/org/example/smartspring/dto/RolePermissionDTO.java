package org.example.smartspring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RolePermissionDTO {
    private String roleId;
    private String roleName;
    private String roleDescription;
    private List<PermissionDTO> permissions; // Utilisation de List pour correspondre au collect(Collectors.toList())
    private int totalPermissions;
}