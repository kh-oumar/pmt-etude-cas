package com.codesolutions.pmt_backend.testRepository;

import com.codesolutions.pmt_backend.entity.*;
import com.codesolutions.pmt_backend.repository.TaskRepository;
import com.codesolutions.pmt_backend.repository.ProjectRepository;
import com.codesolutions.pmt_backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testCreateAndFindTask() {
        // Création d'un projet
        Project project = new Project("Projet Tâche", "Projet pour tester les tâches", LocalDate.now());
        project = projectRepository.save(project);
        assertNotNull(project.getId());

        // Création d'un utilisateur qui sera assigné
        User user = new User("assignee", "assignee@example.com", "password");
        user = userRepository.save(user);
        assertNotNull(user.getId());

        // Création d'une tâche assignée à l'utilisateur
        Task task = new Task(project, "Tâche Test", "Description de la tâche", LocalDate.now().plusDays(7), 3, "to-do", user);
        Task savedTask = taskRepository.save(task);
        assertNotNull(savedTask.getId());

        // Recherche par projet
        List<Task> tasksByProject = taskRepository.findByProject(project);
        assertFalse(tasksByProject.isEmpty(), "Il doit y avoir des tâches pour le projet");

        // Recherche par statut
        List<Task> tasksByStatus = taskRepository.findByStatus("to-do");
        assertFalse(tasksByStatus.isEmpty(), "Il doit y avoir des tâches avec le statut 'to-do'");

        // Recherche par utilisateur assigné
        List<Task> tasksByAssignee = taskRepository.findByAssignedTo(user);
        assertFalse(tasksByAssignee.isEmpty(), "Il doit y avoir des tâches assignées à l'utilisateur");
    }
}
