package com.codesolutions.pmt_backend.service;

import com.codesolutions.pmt_backend.entity.Task;
import com.codesolutions.pmt_backend.entity.TaskHistory;
import com.codesolutions.pmt_backend.entity.User;
import com.codesolutions.pmt_backend.entity.Project;
import com.codesolutions.pmt_backend.repository.TaskHistoryRepository;
import com.codesolutions.pmt_backend.repository.TaskRepository;
import com.codesolutions.pmt_backend.repository.ProjectRepository;
import com.codesolutions.pmt_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskHistoryRepository taskHistoryRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public TaskService(TaskRepository taskRepository, TaskHistoryRepository taskHistoryRepository,
                       ProjectRepository projectRepository, UserRepository userRepository, NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.taskHistoryRepository = taskHistoryRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    /**
     * Crée une nouvelle tâche pour un projet. Si l'assignation est réalisée, enregistre une entrée dans l'historique.
     */
    @Transactional
    public Task createTask(Task task) {
        // Vérification de l'existence du projet référencé par la tâche
        if (task.getProject() == null || task.getProject().getId() == null) {
            throw new IllegalArgumentException("La tâche doit référencer un projet existant");
        }

        Long projectId = task.getProject().getId();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Projet avec id " + projectId + " n'existe pas"));

        // Association du projet existant à la tâche
        task.setProject(project);

        // Création de la tâche
        Task savedTask = taskRepository.save(task);

        // Enregistrement d'une entrée dans l'historique
        TaskHistory history = new TaskHistory(savedTask,
                task.getAssignedTo() != null ? task.getAssignedTo() : null,
                "Tâche créée");
        taskHistoryRepository.save(history);

        return savedTask;
    }

    /**
     * Met à jour une tâche et enregistre l'historique des modifications.
     */
    @Transactional
    public Task updateTask(Task task) {
        Optional<Task> optionalTask = taskRepository.findById(task.getId());
        if (optionalTask.isEmpty()) {
            throw new IllegalArgumentException("La tâche n'existe pas");
        }
        Task existingTask = optionalTask.get();

        if (!existingTask.getName().equals(task.getName())) {
            TaskHistory history = new TaskHistory(existingTask,
                    task.getAssignedTo() != null ? task.getAssignedTo() : null,
                    "Le nom a été changé de \"" + existingTask.getName() + "\" en \"" + task.getName() + "\"");
            taskHistoryRepository.save(history);
        }

        // Pour la description
        if (task.getDescription() != null && !task.getDescription().equals(existingTask.getDescription())) {
            TaskHistory history = new TaskHistory(existingTask,
                    task.getAssignedTo() != null ? task.getAssignedTo() : null,
                    "La description a été changée de \"" + existingTask.getDescription() + "\" en \"" + task.getDescription() + "\"");
            taskHistoryRepository.save(history);
        }

        // Pour la date d'échéance
        if (task.getDueDate() != null && !task.getDueDate().equals(existingTask.getDueDate())) {
            TaskHistory history = new TaskHistory(existingTask,
                    task.getAssignedTo() != null ? task.getAssignedTo() : null,
                    "La date d'échéance a été modifiée de \"" + existingTask.getDueDate() + "\" en \"" + task.getDueDate() + "\"");
            taskHistoryRepository.save(history);
        }

        // Pour la priorité
        if (task.getPriority() != null && !task.getPriority().equals(existingTask.getPriority())) {
            TaskHistory history = new TaskHistory(existingTask,
                    task.getAssignedTo() != null ? task.getAssignedTo() : null,
                    "La priorité a été changée de \"" + existingTask.getPriority() + "\" en \"" + task.getPriority() + "\"");
            taskHistoryRepository.save(history);
        }

        // Pour le statut
        if (task.getStatus() != null && !task.getStatus().equals(existingTask.getStatus())) {
            TaskHistory history = new TaskHistory(existingTask,
                    task.getAssignedTo() != null ? task.getAssignedTo() : null,
                    "Le statut a été changé de \"" + existingTask.getStatus() + "\" à \"" + task.getStatus() + "\"");
            taskHistoryRepository.save(history);
        }

        // Mise à jour de l'objet existant
        existingTask.setName(task.getName());
        existingTask.setDescription(task.getDescription());
        existingTask.setDueDate(task.getDueDate());
        existingTask.setPriority(task.getPriority());
        existingTask.setStatus(task.getStatus());
        existingTask.setAssignedTo(task.getAssignedTo());

        return taskRepository.save(existingTask);
    }

    /**
     * Récupère une tâche par son identifiant.
     */
    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("La tâche avec l'id " + taskId + " n'existe pas"));
    }

    /**
     * Récupère la liste des tâches pour un projet donné.
     */
    public List<Task> getTasksByProjectId(Long projectId) {
        // Si ton repository dispose d'une méthode findByProject(Project project) ou similaire :
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Projet avec id " + projectId + " non trouvé"));
        return taskRepository.findByProject(project);
    }

    @Transactional
    public Task assignTask(Long taskId, Long userId) {
        // Récupérer la tâche
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("La tâche avec l'id " + taskId + " n'existe pas"));

        // Récupérer l'utilisateur qui va être assigné
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur avec id " + userId + " non trouvé"));

        // Mettre à jour le champ assignedTo
        task.setAssignedTo(user);

        // Enregistrer un historique de cette assignation
        TaskHistory history = new TaskHistory(task, user, "Tâche assignée à " + user.getUsername());
        taskHistoryRepository.save(history);

        // Sauvegarder la tâche et envoyer la notification
        Task updatedTask = taskRepository.save(task);
        notificationService.sendAssignmentNotification(user, updatedTask);

        return updatedTask;
    }

    public List<TaskHistory> getTaskHistory(Long taskId) {
        Task task = getTaskById(taskId);
        return taskHistoryRepository.findByTaskOrderByChangeDateDesc(task);
    }
}
