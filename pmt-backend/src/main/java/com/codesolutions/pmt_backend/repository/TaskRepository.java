package com.codesolutions.pmt_backend.repository;

import com.codesolutions.pmt_backend.entity.Task;
import com.codesolutions.pmt_backend.entity.Project;
import com.codesolutions.pmt_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Récupérer toutes les tâches appartenant à un projet
    List<Task> findByProject(Project project);

    // Récupérer toutes les tâches avec un statut donné
    List<Task> findByStatus(String status);

    // Récupérer toutes les tâches assignées à un utilisateur
    List<Task> findByAssignedTo(User user);
}
