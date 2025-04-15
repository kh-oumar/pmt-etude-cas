package com.codesolutions.pmt_backend.testRepository;

import com.codesolutions.pmt_backend.entity.*;
import com.codesolutions.pmt_backend.repository.TaskHistoryRepository;
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
public class TaskHistoryRepositoryTest {

    @Autowired
    private TaskHistoryRepository taskHistoryRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testCreateAndFindTaskHistory() {
        // Préparer les dépendances : projet, utilisateur, tâche
        Project project = new Project("Projet Historique", "Test historique", LocalDate.now());
        project = projectRepository.save(project);
        assertNotNull(project.getId());

        User user = new User("historyUser", "history@example.com", "password");
        user = userRepository.save(user);
        assertNotNull(user.getId());

        Task task = new Task(project, "Tâche Historique", "Test de l'historique", LocalDate.now().plusDays(5), 2, "in progress", user);
        task = taskRepository.save(task);
        assertNotNull(task.getId());

        // Créer plusieurs entrées d'historique
        TaskHistory history1 = new TaskHistory(task, user, "Création de la tâche");
        TaskHistory history2 = new TaskHistory(task, user, "Mise à jour du statut");
        taskHistoryRepository.save(history1);
        taskHistoryRepository.save(history2);

        // Récupérer l'historique trié par date décroissante
        List<TaskHistory> historyList = taskHistoryRepository.findByTaskOrderByChangeDateDesc(task);
        assertFalse(historyList.isEmpty(), "L'historique ne doit pas être vide");
        // On peut vérifier que le premier élément a la date la plus récente
        assertTrue(historyList.get(0).getChangeDate().isAfter(historyList.get(historyList.size()-1).getChangeDate())
                        || historyList.get(0).getChangeDate().equals(historyList.get(historyList.size()-1).getChangeDate()),
                "L'historique doit être trié par date décroissante");
    }
}
