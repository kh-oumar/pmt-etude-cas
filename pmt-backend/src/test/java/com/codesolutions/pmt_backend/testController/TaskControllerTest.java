package com.codesolutions.pmt_backend.testController;

import com.codesolutions.pmt_backend.controller.TaskController;
import com.codesolutions.pmt_backend.entity.Project;
import com.codesolutions.pmt_backend.entity.Task;
import com.codesolutions.pmt_backend.entity.TaskHistory;
import com.codesolutions.pmt_backend.entity.User;
import com.codesolutions.pmt_backend.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @Test
    @DisplayName("POST /api/tasks -> 201 + created task")
    void createTask_success() throws Exception {
        Map<String, Object> payload = Map.of(
                "project", Map.of("id", 3L),
                "name", "T1"
        );
        String json = objectMapper.writeValueAsString(payload);

        Project p = new Project(); p.setId(3L); p.setName("P");
        Task saved = new Task();
        saved.setId(100L);
        saved.setName("T1");
        saved.setProject(p);

        when(taskService.createTask(any(Task.class))).thenReturn(saved);

        mvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.name").value("T1"));
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} -> 200 + updated task")
    void updateTask_success() throws Exception {
        Map<String, Object> payload = Map.of("name", "Updated");
        String json = objectMapper.writeValueAsString(payload);

        Task out = new Task();
        out.setId(7L);
        out.setName("Updated");

        when(taskService.updateTask(argThat(t -> t.getId() == 7L && "Updated".equals(t.getName()))))
                .thenReturn(out);

        mvc.perform(put("/api/tasks/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} -> 200 + task by id")
    void getTaskById_success() throws Exception {
        Task t = new Task();
        t.setId(8L);
        t.setName("Task8");
        when(taskService.getTaskById(8L)).thenReturn(t);

        mvc.perform(get("/api/tasks/8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(8))
                .andExpect(jsonPath("$.name").value("Task8"));
    }

    @Test
    @DisplayName("GET /api/tasks/project/{projectId} -> 200 + list tasks")
    void getTasksByProject_success() throws Exception {
        Task t1 = new Task(); t1.setId(9L); t1.setName("A");
        when(taskService.getTasksByProjectId(9L)).thenReturn(List.of(t1));

        mvc.perform(get("/api/tasks/project/9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(9));
    }

    @Test
    @DisplayName("PUT /api/tasks/{id}/assign -> 200 + assigned task")
    void assignTask_success() throws Exception {
        User u = new User(); u.setId(42L);
        Task assigned = new Task(); assigned.setId(5L); assigned.setAssignedTo(u);
        when(taskService.assignTask(5L, 42L)).thenReturn(assigned);

        mvc.perform(put("/api/tasks/5/assign").param("userId", "42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.assignedTo.id").value(42));
    }

    @Test
    @DisplayName("GET /api/tasks/{id}/history -> 200 + history list")
    void getTaskHistory_success() throws Exception {
        TaskHistory h = new TaskHistory();
        h.setId(101L);
        when(taskService.getTaskHistory(5L)).thenReturn(List.of(h));

        mvc.perform(get("/api/tasks/5/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(101));
    }
}
