package com.codesolutions.pmt_backend.repository;

import com.codesolutions.pmt_backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    // Recherche d'un rôle par nom (ex: "ADMIN", "MEMBER", "OBSERVER")
    Optional<Role> findByName(String name);
}
