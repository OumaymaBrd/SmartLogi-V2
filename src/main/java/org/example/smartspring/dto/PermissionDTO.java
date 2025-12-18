package org.example.smartspring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionDTO {

    private String id;
    private String name;
    private String description;
    private String category;
    private LocalDateTime createdAt;
}
