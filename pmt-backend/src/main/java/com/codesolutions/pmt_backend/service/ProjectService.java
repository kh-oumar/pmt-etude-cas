package com.codesolutions.pmt_backend.service;

import com.codesolutions.pmt_backend.entity.*;
import com.codesolutions.pmt_backend.repository.ProjectMemberRepository;
import com.codesolutions.pmt_backend.repository.ProjectRepository;
import com.codesolutions.pmt_backend.repository.RoleRepository;
import com.codesolutions.pmt_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, ProjectMemberRepository projectMemberRepository,
                          RoleRepository roleRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    /**
     * Crée un nouveau projet et associe l'utilisateur créateur comme ADMIN
     */
    @Transactional
    public Project createProject(Project project, Long creatorId) {
        // Sauvegarde du projet
        Project createdProject = projectRepository.save(project);

        // Récupère l'utilisateur par son ID
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        // Récupère le rôle ADMIN
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("Rôle ADMIN introuvable"));

        // Crée l'association ProjectMember et l'enregistre
        ProjectMember projectMember = new ProjectMember(creator, createdProject, adminRole);
        projectMemberRepository.save(projectMember);

        return createdProject;
    }

    /**
     * Récupère la liste de tous les projets.
     */
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Projet avec id " + id + " non trouvé"));
    }


    /**
     * Récupère la liste des projets associés à un utilisateur (créés ou auxquels il est membre).
     */
    @Transactional(readOnly = true)
    public List<Project> getProjectsByUser(Long userId) {
        List<ProjectMember> memberships = projectMemberRepository.findByUserId(userId);
        return memberships.stream()
                .map(ProjectMember::getProject)
                .distinct()
                .collect(Collectors.toList());
    }

}
