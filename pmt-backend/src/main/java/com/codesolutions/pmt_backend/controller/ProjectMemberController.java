package com.codesolutions.pmt_backend.controller;

import com.codesolutions.pmt_backend.entity.Project;
import com.codesolutions.pmt_backend.entity.ProjectMember;
import com.codesolutions.pmt_backend.entity.ProjectMemberId;
import com.codesolutions.pmt_backend.entity.Role;
import com.codesolutions.pmt_backend.entity.User;
import com.codesolutions.pmt_backend.repository.ProjectMemberRepository;
import com.codesolutions.pmt_backend.repository.ProjectRepository;
import com.codesolutions.pmt_backend.repository.RoleRepository;
import com.codesolutions.pmt_backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectMemberController {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public ProjectMemberController(ProjectRepository projectRepository,
                                   UserRepository userRepository,
                                   RoleRepository roleRepository,
                                   ProjectMemberRepository projectMemberRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    /**
     * Endpoint pour lister les membres d'un projet.
     * Exemple d'appel : GET /api/projects/{projectId}/members
     */
    @GetMapping("/{projectId}/members")
    public ResponseEntity<List<ProjectMember>> getProjectMembers(@PathVariable Long projectId) {
        List<ProjectMember> members = projectMemberRepository.findByProjectId(projectId);
        return ResponseEntity.ok(members);
    }

    /**
     * Endpoint pour mettre à jour le rôle d'un membre dans un projet.
     * Exemple d'appel : PUT /api/projects/{projectId}/members/{userId}?role=MEMBER
     */
    @PutMapping("/{projectId}/members/{userId}")
    public ResponseEntity<ProjectMember> updateMemberRole(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            @RequestParam String role) {

        // Vérifier que le projet existe
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Projet avec id " + projectId + " non trouvé"));

        // Vérifier que l'utilisateur existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur avec id " + userId + " non trouvé"));

        // Récupérer le rôle par son nom (par exemple, "MEMBER", "ADMIN", "OBSERVER")
        Role newRole = roleRepository.findByName(role)
                .orElseThrow(() -> new IllegalArgumentException("Rôle " + role + " non trouvé"));

        // Rechercher l'association existante pour ce membre dans le projet
        ProjectMemberId pmId = new ProjectMemberId(userId, projectId);
        ProjectMember pm = projectMemberRepository.findById(pmId)
                .orElseThrow(() -> new IllegalArgumentException("Aucune association trouvée pour cet utilisateur dans ce projet"));

        // Mettre à jour le rôle et sauvegarder l'association
        pm.setRole(newRole);
        ProjectMember updated = projectMemberRepository.save(pm);
        return ResponseEntity.ok(updated);
    }
}
