package com.codesolutions.pmt_backend.testController;

import com.codesolutions.pmt_backend.controller.ProjectMemberController;
import com.codesolutions.pmt_backend.entity.Project;
import com.codesolutions.pmt_backend.entity.ProjectMember;
import com.codesolutions.pmt_backend.entity.ProjectMemberId;
import com.codesolutions.pmt_backend.entity.Role;
import com.codesolutions.pmt_backend.entity.User;
import com.codesolutions.pmt_backend.repository.ProjectMemberRepository;
import com.codesolutions.pmt_backend.repository.ProjectRepository;
import com.codesolutions.pmt_backend.repository.RoleRepository;
import com.codesolutions.pmt_backend.repository.UserRepository;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectMemberController.class)
class ProjectMemberControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean private ProjectRepository projectRepository;
    @MockBean private UserRepository userRepository;
    @MockBean private RoleRepository roleRepository;
    @MockBean private ProjectMemberRepository projectMemberRepository;

    //--- GET /api/projects/{projectId}/members ---//

    @Test @DisplayName("GET members: retourne liste vide")
    void getProjectMembers_empty() throws Exception {
        when(projectMemberRepository.findByProjectId(123L)).thenReturn(List.of());

        mvc.perform(get("/api/projects/123/members"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test @DisplayName("GET members: retourne deux membres")
    void getProjectMembers_two() throws Exception {
        User u1 = new User(); u1.setId(1L); u1.setUsername("A");
        User u2 = new User(); u2.setId(2L); u2.setUsername("B");
        Project p = new Project(); p.setId(123L);
        Role r = new Role("MEMBER");
        ProjectMember m1 = new ProjectMember(u1, p, r);
        ProjectMember m2 = new ProjectMember(u2, p, r);

        when(projectMemberRepository.findByProjectId(123L))
                .thenReturn(List.of(m1, m2));

        mvc.perform(get("/api/projects/123/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].user.id").value(1))
                .andExpect(jsonPath("$[1].user.id").value(2));
    }

    //--- PUT /api/projects/{projectId}/members/{userId}?role=… ---//

    @Test @DisplayName("PUT updateMemberRole: projet introuvable → exception")
    void updateMemberRole_projNotFound() throws Exception {
        when(projectRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ServletException.class, () ->
                mvc.perform(put("/api/projects/5/members/10").param("role", "ADMIN"))
        );
    }

    @Test @DisplayName("PUT updateMemberRole: user introuvable → exception")
    void updateMemberRole_userNotFound() throws Exception {
        when(projectRepository.findById(5L)).thenReturn(Optional.of(new Project()));
        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ServletException.class, () ->
                mvc.perform(put("/api/projects/5/members/10").param("role", "ADMIN"))
        );
    }

    @Test @DisplayName("PUT updateMemberRole: rôle introuvable → exception")
    void updateMemberRole_roleNotFound() throws Exception {
        when(projectRepository.findById(5L)).thenReturn(Optional.of(new Project()));
        when(userRepository.findById(10L)).thenReturn(Optional.of(new User()));
        when(roleRepository.findByName("X")).thenReturn(Optional.empty());

        assertThrows(ServletException.class, () ->
                mvc.perform(put("/api/projects/5/members/10").param("role", "X"))
        );
    }

    @Test @DisplayName("PUT updateMemberRole: association introuvable → exception")
    void updateMemberRole_memberMissing() throws Exception {
        Project proj = new Project(); proj.setId(6L);
        User user = new User(); user.setId(11L);

        when(projectRepository.findById(6L)).thenReturn(Optional.of(proj));
        when(userRepository.findById(11L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("MEMBER")).thenReturn(Optional.of(new Role("MEMBER")));
        when(projectMemberRepository.findById(new ProjectMemberId(11L, 6L)))
                .thenReturn(Optional.empty());

        assertThrows(ServletException.class, () ->
                mvc.perform(put("/api/projects/6/members/11").param("role", "MEMBER"))
        );
    }

    @Test @DisplayName("PUT updateMemberRole: succès → 200 + body JSON")
    void updateMemberRole_success() throws Exception {
        Project proj = new Project(); proj.setId(7L);
        User user = new User(); user.setId(12L);
        Role newRole = new Role("ADMIN");

        ProjectMember pm = new ProjectMember(user, proj, new Role("MEMBER"));
        pm.setRole(newRole);

        when(projectRepository.findById(7L)).thenReturn(Optional.of(proj));
        when(userRepository.findById(12L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(newRole));
        when(projectMemberRepository.findById(new ProjectMemberId(12L, 7L)))
                .thenReturn(Optional.of(pm));
        when(projectMemberRepository.save(pm)).thenReturn(pm);

        mvc.perform(put("/api/projects/7/members/12").param("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.project.id").value(7))
                .andExpect(jsonPath("$.user.id").value(12))
                .andExpect(jsonPath("$.role.name").value("ADMIN"));
    }
}
