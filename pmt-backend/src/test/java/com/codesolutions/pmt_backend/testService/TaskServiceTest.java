package com.codesolutions.pmt_backend.testService;

import com.codesolutions.pmt_backend.entity.Project;
import com.codesolutions.pmt_backend.entity.Task;
import com.codesolutions.pmt_backend.entity.TaskHistory;
import com.codesolutions.pmt_backend.entity.User;
import com.codesolutions.pmt_backend.repository.ProjectRepository;
import com.codesolutions.pmt_backend.repository.TaskHistoryRepository;
import com.codesolutions.pmt_backend.repository.TaskRepository;
import com.codesolutions.pmt_backend.repository.UserRepository;
import com.codesolutions.pmt_backend.service.NotificationService;
import com.codesolutions.pmt_backend.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock private TaskRepository taskRepository;
    @Mock private TaskHistoryRepository taskHistoryRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private UserRepository userRepository;
    @Mock private NotificationService notificationService;

    @InjectMocks private TaskService service;

    //--- createTask() ---//

    @Test @DisplayName("createTask: pas de projet → IllegalArgument")
    void createTask_noProject_throws() {
        Task t = new Task();
        t.setProject(null);

        assertThatThrownBy(() -> service.createTask(t))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("référencer un projet");
    }

    @Test @DisplayName("createTask: projet inconnu → IllegalArgument")
    void createTask_projNotFound_throws() {
        Task t = new Task();
        Project p = new Project();
        p.setId(42L);
        t.setProject(p);

        when(projectRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createTask(t))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Projet avec id 42");
    }

    @Test @DisplayName("createTask: flux nominal → history enregistré")
    void createTask_success_savesHistory() {
        // given
        Project inputProj = new Project();
        inputProj.setId(1L);
        Task in = new Task();
        in.setProject(inputProj);

        Project realProj = new Project();
        realProj.setId(1L);
        realProj.setName("Mon Projet");
        when(projectRepository.findById(1L)).thenReturn(Optional.of(realProj));

        Task saved = new Task();
        saved.setId(100L);
        // on stub save(any(Task))
        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        // when
        Task out = service.createTask(in);

        // then
        assertThat(out.getId()).isEqualTo(100L);
        verify(taskHistoryRepository).save(argThat(h ->
                h.getTask().equals(saved)
                        && "Tâche créée".equals(h.getChangeDescription())
        ));
    }

    //--- updateTask() ---//

    @Test @DisplayName("updateTask: tâche absente → IllegalArgument")
    void updateTask_notFound_throws() {
        Task t = new Task();
        t.setId(9L);
        when(taskRepository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateTask(t))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("n'existe pas");
    }

    @Test @DisplayName("updateTask: seulement nom modifié → history enregistré")
    void updateTask_nameChange_recordsHistory() {
        // existing
        Task old = new Task();
        old.setId(1L);
        old.setName("Ancien");
        old.setDescription("Desc");
        old.setDueDate(null);
        old.setPriority(null);
        old.setStatus(null);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(old));

        // update dto
        Task upd = new Task();
        upd.setId(1L);
        upd.setName("Nouveau");
        upd.setDescription("Desc");  // même valeur, pas de history ici

        Task saved = new Task();
        saved.setId(1L);
        saved.setName("Nouveau");
        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        Task out = service.updateTask(upd);

        assertThat(out.getName()).isEqualTo("Nouveau");
        verify(taskHistoryRepository).save(argThat(h ->
                h.getChangeDescription().contains("nom a été changé")
        ));
    }

    //--- getTaskById() ---//

    @Test @DisplayName("getTaskById: inexistant → IllegalArgument")
    void getTaskById_notFound_throws() {
        when(taskRepository.findById(5L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getTaskById(5L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("5");
    }

    @Test @DisplayName("getTaskById: existant → retourne la tâche")
    void getTaskById_found() {
        Task t = new Task();
        t.setId(2L);
        when(taskRepository.findById(2L)).thenReturn(Optional.of(t));
        assertThat(service.getTaskById(2L)).isSameAs(t);
    }

    //--- getTasksByProjectId() ---//

    @Test @DisplayName("getTasksByProjectId: projet inconnu → IllegalArgument")
    void getTasksByProjectId_projNotFound_throws() {
        when(projectRepository.findById(7L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getTasksByProjectId(7L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("getTasksByProjectId: flux nominal")
    void getTasksByProjectId_success() {
        Project p = new Project();
        p.setId(3L);
        when(projectRepository.findById(3L)).thenReturn(Optional.of(p));

        Task t1 = new Task(); t1.setId(10L);
        List<Task> list = List.of(t1);
        when(taskRepository.findByProject(p)).thenReturn(list);

        List<Task> out = service.getTasksByProjectId(3L);
        assertThat(out).containsExactlyElementsOf(list);
    }

    //--- assignTask() ---//

    @Test @DisplayName("assignTask: tâche absente → IllegalArgument")
    void assignTask_taskNotFound_throws() {
        when(taskRepository.findById(11L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.assignTask(11L, 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("assignTask: utilisateur absent → IllegalArgument")
    void assignTask_userNotFound_throws() {
        Task t = new Task();
        t.setId(11L);
        when(taskRepository.findById(11L)).thenReturn(Optional.of(t));
        when(userRepository.findById(22L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.assignTask(11L, 22L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("assignTask: flux nominal → history + notification")
    void assignTask_success() {
        Task t = new Task();
        t.setId(8L);
        when(taskRepository.findById(8L)).thenReturn(Optional.of(t));

        User u = new User();
        u.setId(99L);
        u.setUsername("bob");
        u.setEmail("bob@example.com");
        when(userRepository.findById(99L)).thenReturn(Optional.of(u));

        Task saved = new Task();
        saved.setId(8L);
        saved.setAssignedTo(u);
        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        Task out = service.assignTask(8L, 99L);

        assertThat(out.getAssignedTo()).isSameAs(u);
        verify(taskHistoryRepository).save(argThat(h ->
                h.getChangeDescription().contains("assignée")
        ));
        verify(notificationService).sendAssignmentNotification(u, saved);
    }

    //--- getTaskHistory() ---//

    @Test @DisplayName("getTaskHistory: retourne la liste triée")
    void getTaskHistory_returnsList() {
        Task t = new Task();
        t.setId(5L);
        when(taskRepository.findById(5L)).thenReturn(Optional.of(t));

        TaskHistory h1 = new TaskHistory(t, null, "x");
        TaskHistory h2 = new TaskHistory(t, null, "y");
        List<TaskHistory> histos = List.of(h1, h2);
        when(taskHistoryRepository.findByTaskOrderByChangeDateDesc(t)).thenReturn(histos);

        List<TaskHistory> out = service.getTaskHistory(5L);
        assertThat(out).containsExactly(h1, h2);
    }

    @Test
    @DisplayName("updateTask: description modifiée enregistre l'historique")
    void updateTask_descriptionChange_recordsHistory() {
        // Prépare la tâche existante en base
        Task existing = new Task();
        existing.setId(1L);
        existing.setName("Nom");
        existing.setDescription("Ancienne");
        existing.setDueDate(null);
        existing.setPriority(null);
        existing.setStatus(null);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));

        // Prépare la requête de mise à jour : seul le champ description change
        Task toUpdate = new Task();
        toUpdate.setId(1L);
        toUpdate.setName("Nom");            // même nom => pas de history pour le nom
        toUpdate.setDescription("Nouvelle"); // description changée

        // Stub de la sauvegarde finale
        Task saved = new Task();
        saved.setId(1L);
        saved.setDescription("Nouvelle");
        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        // Appel de la méthode
        Task result = service.updateTask(toUpdate);

        // Vérifie qu'on a bien enregistré un historique pour la description
        verify(taskHistoryRepository).save(argThat(h ->
                h.getTask().equals(existing) &&
                        h.getChangeDescription().contains("description a été changée")
        ));

        // Vérifie que le résultat est bien celui renvoyé par le repo
        assertThat(result.getDescription()).isEqualTo("Nouvelle");
    }

    @Test
    @DisplayName("updateTask: dueDate modifiée enregistre l'historique")
    void updateTask_dueDateChange_recordsHistory() {
        // Tâche existante
        Task existing = new Task();
        existing.setId(2L);
        existing.setName("Nom");
        existing.setDescription("Desc");
        existing.setDueDate(LocalDate.of(2025, 5, 1));
        existing.setPriority(null);
        existing.setStatus(null);
        when(taskRepository.findById(2L)).thenReturn(Optional.of(existing));

        // Mise à jour : on ne change que la dueDate
        Task toUpdate = new Task();
        toUpdate.setId(2L);
        toUpdate.setName("Nom");
        toUpdate.setDescription("Desc");
        toUpdate.setDueDate(LocalDate.of(2025, 6, 1));  // nouvelle date

        // Stub de save
        Task saved = new Task();
        saved.setId(2L);
        saved.setDueDate(LocalDate.of(2025, 6, 1));
        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        // Appel
        Task result = service.updateTask(toUpdate);

        // Vérification de l'historique
        verify(taskHistoryRepository).save(argThat(h ->
                h.getTask().equals(existing) &&
                        h.getChangeDescription().contains("date d'échéance")
        ));

        // Vérification du résultat
        assertThat(result.getDueDate()).isEqualTo(LocalDate.of(2025, 6, 1));
    }

    @Test
    @DisplayName("updateTask: priority modifiée enregistre l'historique")
    void updateTask_priorityChange_recordsHistory() {
        // Tâche existante
        Task existing = new Task();
        existing.setId(3L);
        existing.setName("Nom");
        existing.setDescription("Desc");
        existing.setDueDate(null);
        existing.setPriority(1);
        existing.setStatus(null);
        when(taskRepository.findById(3L)).thenReturn(Optional.of(existing));

        // Mise à jour : on change uniquement la priorité
        Task toUpdate = new Task();
        toUpdate.setId(3L);
        toUpdate.setName("Nom");
        toUpdate.setDescription("Desc");
        toUpdate.setDueDate(null);
        toUpdate.setPriority(2);  // nouvelle priorité

        // Stub du save final
        Task saved = new Task();
        saved.setId(3L);
        saved.setPriority(2);
        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        // Appel de la méthode
        Task result = service.updateTask(toUpdate);

        // Vérification de l'historique
        verify(taskHistoryRepository).save(argThat(h ->
                h.getTask().equals(existing) &&
                        h.getChangeDescription().contains("priorité a été changée")
        ));

        // Vérification du résultat
        assertThat(result.getPriority()).isEqualTo(2);
    }

    @Test @DisplayName("updateTask: status modifié")
    void updateTask_statusChange_recordsHistory() {
        Task existing = new Task();
        existing.setId(5L);
        existing.setName("N"); existing.setDescription("D");
        existing.setStatus("OLD");
        when(taskRepository.findById(5L)).thenReturn(Optional.of(existing));

        Task upd = new Task();
        upd.setId(5L);
        upd.setName("N"); upd.setDescription("D");
        upd.setStatus("NEW");

        Task saved = new Task(); saved.setId(5L); saved.setStatus("NEW");
        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        service.updateTask(upd);

        verify(taskHistoryRepository).save(argThat(h ->
                h.getChangeDescription().contains("Le statut a été changé")
        ));
    }
}
