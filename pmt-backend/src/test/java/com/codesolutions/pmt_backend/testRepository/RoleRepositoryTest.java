package com.codesolutions.pmt_backend.testRepository;

import com.codesolutions.pmt_backend.entity.Role;
import com.codesolutions.pmt_backend.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testCreateAndFindRole() {
        // Création d'un rôle
        Role role = new Role("ADMIN");
        Role savedRole = roleRepository.save(role);
        assertNotNull(savedRole.getId(), "Le rôle doit avoir un ID après sauvegarde");

        // Recherche par nom
        Role foundRole = roleRepository.findByName("ADMIN").orElse(null);
        assertNotNull(foundRole, "Le rôle doit être retrouvé par son nom");
        assertEquals("ADMIN", foundRole.getName(), "Le nom du rôle doit correspondre");
    }
}
