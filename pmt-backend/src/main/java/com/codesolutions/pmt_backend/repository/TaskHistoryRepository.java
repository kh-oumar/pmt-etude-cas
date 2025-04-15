package com.codesolutions.pmt_backend.repository;

import com.codesolutions.pmt_backend.entity.TaskHistory;
import com.codesolutions.pmt_backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {

    // Récupérer l'historique d'une tâche trié par date décroissante
    List<TaskHistory> findByTaskOrderByChangeDateDesc(Task task);
}
