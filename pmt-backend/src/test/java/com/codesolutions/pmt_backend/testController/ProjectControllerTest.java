package com.codesolutions.pmt_backend.testController;

import com.codesolutions.pmt_backend.controller.ProjectController;
import com.codesolutions.pmt_backend.entity.Project;
import com.codesolutions.pmt_backend.repository.ProjectMemberRepository;
import com.codesolutions.pmt_backend.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private ProjectMemberRepository projectMemberRepository;

    @Test
    public void testCreateProjectEndpoint() throws Exception {
        LocalDate today = LocalDate.now();
        Project project = new Project("Projet Test", "Description Test", today);
        Project savedProject = new Project("Projet Test", "Description Test", today);
        savedProject.setId(1L);

        when(projectService.createProject(any(Project.class), eq(1L))).thenReturn(savedProject);

        mockMvc.perform(post("/api/projects?creatorId=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Projet Test"));
    }
}
