package org.example.smartspring.security.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.smartspring.entities.Permission;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();
}
