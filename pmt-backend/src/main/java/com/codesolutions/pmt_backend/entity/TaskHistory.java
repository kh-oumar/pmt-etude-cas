package com.codesolutions.pmt_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@Table(name = "task_history")
public class TaskHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Référence à la tâche concernée
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Task task;

    // Référence à l'utilisateur ayant effectué le changement
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;

    // Date de la modification (définie par défaut lors de la création)
    private LocalDateTime changeDate = LocalDateTime.now();

    @Column(length = 500)
    private String changeDescription;

    public TaskHistory() {
    }

    public TaskHistory(Task task, User user, String changeDescription) {
        this.task = task;
        this.user = user;
        this.changeDescription = changeDescription;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Task getTask() {
        return task;
    }
    public void setTask(Task task) {
        this.task = task;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public LocalDateTime getChangeDate() {
        return changeDate;
    }
    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }
    public String getChangeDescription() {
        return changeDescription;
    }
    public void setChangeDescription(String changeDescription) {
        this.changeDescription = changeDescription;
    }
}
