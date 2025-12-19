package org.example.smartspring.security.repository;

import org.example.smartspring.security.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByName(String name);

    @Query("SELECT DISTINCT r FROM Role r " +
            "LEFT JOIN FETCH r.permissions " +
            "WHERE r.id = :roleId")
    Optional<Role> findByIdWithPermissions(@Param("roleId") String roleId);
}
