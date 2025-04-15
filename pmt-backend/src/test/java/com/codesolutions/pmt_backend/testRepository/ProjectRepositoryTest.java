package com.codesolutions.pmt_backend.testRepository;

import com.codesolutions.pmt_backend.entity.Project;
import com.codesolutions.pmt_backend.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    public void testCreateAndFindProject() {
        // Crée une instance de project
        Project project = new Project("Projet Test", "Description du projet test", LocalDate.now());

        // Sauvegarde en base
        Project savedProject = projectRepository.save(project);

        // Vérifie que l'ID a été généré
        assertNotNull(savedProject.getId(), "L'ID du projet doit être généré");

        // Recherche par ID
        Optional<Project> retrievedProject = projectRepository.findById(savedProject.getId());
        assertTrue(retrievedProject.isPresent(), "Le projet doit être présent en base");
        assertEquals("Projet Test", retrievedProject.get().getName(), "Le nom du projet doit correspondre");
    }
}

