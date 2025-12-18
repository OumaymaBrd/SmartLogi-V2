package org.example.smartspring.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePermissionRequest {

    @NotBlank(message = "Le nom de la permission est requis")
    private String name;

    private String description;

    @NotBlank(message = "La catégorie est requise")
    private String category;
}
