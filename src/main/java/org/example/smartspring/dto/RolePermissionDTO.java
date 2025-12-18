package org.example.smartspring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePermissionDTO {

    private String roleId;
    private String roleName;
    private String roleDescription;
    private Set<PermissionDTO> permissions;
}
