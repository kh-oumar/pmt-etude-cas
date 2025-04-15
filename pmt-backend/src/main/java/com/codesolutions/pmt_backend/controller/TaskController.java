package com.codesolutions.pmt_backend.controller;

import com.codesolutions.pmt_backend.entity.Task;
import com.codesolutions.pmt_backend.entity.TaskHistory;
import com.codesolutions.pmt_backend.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Endpoint pour créer une nouvelle tâche.
     */
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task createdTask = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    /**
     * Endpoint pour mettre à jour une tâche existante.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable("id") Long taskId, @RequestBody Task task) {
        // On s'assure que l'objet Task a bien son id correspondant
        task.setId(taskId);
        Task updatedTask = taskService.updateTask(task);
        return ResponseEntity.ok(updatedTask);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable("id") Long taskId) {
        Task task = taskService.getTaskById(taskId);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Task>> getTasksByProject(@PathVariable("projectId") Long projectId) {
        List<Task> tasks = taskService.getTasksByProjectId(projectId);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<Task> assignTask(@PathVariable("id") Long taskId,
                                           @RequestParam Long userId) {
        Task updatedTask = taskService.assignTask(taskId, userId);
        return ResponseEntity.ok(updatedTask);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<TaskHistory>> getTaskHistory(@PathVariable("id") Long taskId) {
        List<TaskHistory> historyList = taskService.getTaskHistory(taskId);
        return ResponseEntity.ok(historyList);
    }
}
