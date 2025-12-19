package org.example.smartspring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RolePermissionDTO {
    private String roleId;
    private String roleName;
    private String roleDescription;
    private List<PermissionDTO> permissions;
    private int totalPermissions;
}
