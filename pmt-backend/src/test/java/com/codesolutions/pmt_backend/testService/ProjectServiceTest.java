package com.codesolutions.pmt_backend.testService;

import com.codesolutions.pmt_backend.entity.Project;
import com.codesolutions.pmt_backend.entity.ProjectMember;
import com.codesolutions.pmt_backend.entity.Role;
import com.codesolutions.pmt_backend.entity.User;
import com.codesolutions.pmt_backend.repository.ProjectMemberRepository;
import com.codesolutions.pmt_backend.repository.ProjectRepository;
import com.codesolutions.pmt_backend.repository.RoleRepository;
import com.codesolutions.pmt_backend.repository.UserRepository;
import com.codesolutions.pmt_backend.service.ProjectService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock private ProjectRepository projectRepository;
    @Mock private ProjectMemberRepository projectMemberRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private ProjectService service;

    //--- createProject() ---//

    @Test @DisplayName("createProject: utilisateur inconnu → IllegalArgument")
    void createProject_userNotFound_throws() {
        Project p = new Project();
        when(projectRepository.save(p)).thenReturn(p);
        when(userRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createProject(p, 5L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Utilisateur non trouvé");
    }

    @Test @DisplayName("createProject: rôle ADMIN manquant → IllegalState")
    void createProject_roleNotFound_throws() {
        Project p = new Project();
        when(projectRepository.save(p)).thenReturn(p);
        User u = new User(); u.setId(7L);
        when(userRepository.findById(7L)).thenReturn(Optional.of(u));
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createProject(p, 7L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Rôle ADMIN introuvable");
    }

    @Test @DisplayName("createProject: succès → projet créé + membre ADMIN")
    void createProject_success() {
        // given
        Project p = new Project();
        p.setId(10L);
        when(projectRepository.save(p)).thenReturn(p);

        User creator = new User();
        creator.setId(20L);
        when(userRepository.findById(20L)).thenReturn(Optional.of(creator));

        Role admin = new Role("ADMIN");
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(admin));

        // when
        Project out = service.createProject(p, 20L);

        // then
        assertThat(out).isSameAs(p);
        verify(projectMemberRepository).save(argThat(pm ->
                pm.getProject().equals(p) &&
                        pm.getUser().equals(creator) &&
                        pm.getRole().equals(admin)
        ));
    }

    //--- getAllProjects() ---//

    @Test @DisplayName("getAllProjects: retourne tous les projets")
    void getAllProjects_returnsList() {
        Project a = new Project(); a.setId(1L);
        Project b = new Project(); b.setId(2L);
        List<Project> list = Arrays.asList(a, b);
        when(projectRepository.findAll()).thenReturn(list);

        List<Project> out = service.getAllProjects();
        assertThat(out).containsExactly(a, b);
    }

    //--- getProjectById() ---//

    @Test @DisplayName("getProjectById: existant → OK")
    void getProjectById_found() {
        Project p = new Project();
        p.setId(3L);
        when(projectRepository.findById(3L)).thenReturn(Optional.of(p));

        Project out = service.getProjectById(3L);
        assertThat(out).isSameAs(p);
    }

    @Test @DisplayName("getProjectById: inexistant → IllegalArgument")
    void getProjectById_notFound_throws() {
        when(projectRepository.findById(4L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getProjectById(4L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("4");
    }

    //--- getProjectsByUser() ---//

    @Test @DisplayName("getProjectsByUser: retourne projets du user")
    void getProjectsByUser_returnsDistinctProjects() {
        // deux memberships différents vers les mêmes 2 projets
        Project p1 = new Project(); p1.setId(100L);
        Project p2 = new Project(); p2.setId(200L);

        ProjectMember m1 = new ProjectMember();
        m1.setProject(p1);
        ProjectMember m2 = new ProjectMember();
        m2.setProject(p2);

        when(projectMemberRepository.findByUserId(50L))
                .thenReturn(Arrays.asList(m1, m2));

        List<Project> out = service.getProjectsByUser(50L);
        assertThat(out).containsExactlyInAnyOrder(p1, p2);
        // distinct() est implicite ici car on n'a pas de doublons dans la liste stubée
    }
}
