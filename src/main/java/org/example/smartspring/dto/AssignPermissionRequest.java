package org.example.smartspring.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignPermissionRequest {

    @NotEmpty(message = "La liste des permissions ne peut pas être vide")
    private Set<String> permissionIds;
}
