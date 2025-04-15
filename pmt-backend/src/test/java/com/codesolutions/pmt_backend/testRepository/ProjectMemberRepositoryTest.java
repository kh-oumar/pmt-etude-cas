package com.codesolutions.pmt_backend.testRepository;

import com.codesolutions.pmt_backend.entity.*;
import com.codesolutions.pmt_backend.repository.ProjectMemberRepository;
import com.codesolutions.pmt_backend.repository.ProjectRepository;
import com.codesolutions.pmt_backend.repository.RoleRepository;
import com.codesolutions.pmt_backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ProjectMemberRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Test
    public void testCreateAndFindProjectMember() {
        // Création d'un utilisateur
        User user = new User("testuser", "testuser@example.com", "password");
        user = userRepository.save(user);
        assertNotNull(user.getId());

        // Création d'un projet
        Project project = new Project("Projet Association", "Test de l'association", LocalDate.now());
        project = projectRepository.save(project);
        assertNotNull(project.getId());

        // Création d'un rôle
        Role role = new Role("ADMIN");
        role = roleRepository.save(role);
        assertNotNull(role.getId());

        // Création de l'association
        ProjectMember projectMember = new ProjectMember(user, project, role);
        projectMemberRepository.save(projectMember);

        // Vérifie la relation par la clé composite
        ProjectMember retrieved = projectMemberRepository.findById(new ProjectMemberId(user.getId(), project.getId())).orElse(null);
        assertNotNull(retrieved, "La relation ProjectMember doit être retrouvée");
        assertEquals("ADMIN", retrieved.getRole().getName(), "Le rôle doit être 'ADMIN'");

        // Vérifier qu'on peut récupérer les associations pour un utilisateur donné
        List<ProjectMember> membersByUser = projectMemberRepository.findByUser(user);
        assertFalse(membersByUser.isEmpty(), "L'utilisateur doit avoir au moins une association");

        // Vérifier qu'on peut récupérer les associations pour un projet donné
        List<ProjectMember> membersByProject = projectMemberRepository.findByProject(project);
        assertFalse(membersByProject.isEmpty(), "Le projet doit avoir au moins une association");
    }
}
