package com.codesolutions.pmt_backend.testController;

import com.codesolutions.pmt_backend.controller.TaskController;
import com.codesolutions.pmt_backend.entity.Task;
import com.codesolutions.pmt_backend.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean; // Usage temporaire malgré la dépréciation
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @Test
    public void testCreateTask() throws Exception {
        Task task = new Task();
        task.setName("Tâche Test");
        task.setDescription("Description Test");
        task.setDueDate(LocalDate.now().plusDays(7));
        task.setPriority(3);
        task.setStatus("to-do");

        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setName(task.getName());
        savedTask.setDescription(task.getDescription());
        savedTask.setDueDate(task.getDueDate());
        savedTask.setPriority(task.getPriority());
        savedTask.setStatus(task.getStatus());

        when(taskService.createTask(any(Task.class))).thenReturn(savedTask);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Tâche Test"));
    }

    @Test
    public void testUpdateTask() throws Exception {
        Task task = new Task();
        task.setId(1L);
        task.setName("Tâche Test Update");
        task.setStatus("in progress");

        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setName(task.getName());
        updatedTask.setStatus(task.getStatus());

        when(taskService.updateTask(any(Task.class))).thenReturn(updatedTask);

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Tâche Test Update"))
                .andExpect(jsonPath("$.status").value("in progress"));
    }
}
