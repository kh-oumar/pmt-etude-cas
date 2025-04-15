package com.codesolutions.pmt_backend.controller;

import com.codesolutions.pmt_backend.entity.Project;
import com.codesolutions.pmt_backend.entity.ProjectMember;
import com.codesolutions.pmt_backend.repository.ProjectMemberRepository;
import com.codesolutions.pmt_backend.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectMemberRepository projectMemberRepository;
    public ProjectController(ProjectService projectService, ProjectMemberRepository projectMemberRepository) {
        this.projectService = projectService;
        this.projectMemberRepository = projectMemberRepository;}

    /**
     * Endpoint pour créer un nouveau projet et associer l'utilisateur créateur en tant qu'ADMIN.
     * Exemple d'appel : POST /api/projects?creatorId=1
     */
    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Project project,
                                                 @RequestParam Long creatorId) {
        Project createdProject = projectService.createProject(project, creatorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    /**
     * Endpoint pour récupérer tous les projets.
     * Exemple d'appel : GET /api/projects
     */
    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    /**
     * Endpoint pour récupérer un projet par son identifiant.
     * Exemple d'appel : GET /api/projects/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        Project project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    /**
     * Endpoint pour récupérer les projets associés à un utilisateur.
     * Exemple d'appel : GET /api/users/1/projects
     */
    @GetMapping("/users/{userId}/projects")
    public ResponseEntity<List<Project>> getUserProjects(@PathVariable Long userId) {
        List<Project> projects = projectService.getProjectsByUser(userId);
        return ResponseEntity.ok(projects);
    }
}
