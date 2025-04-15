package com.codesolutions.pmt_backend.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "project_member")
public class ProjectMember {

    @EmbeddedId
    private ProjectMemberId id;

    // Relation vers l'utilisateur
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;

    // Relation vers le projet
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("projectId")
    @JoinColumn(name = "project_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Project project;

    // Référence au rôle
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    public ProjectMember() {
    }

    public ProjectMember(User user, Project project, Role role) {
        this.user = user;
        this.project = project;
        this.role = role;
        this.id = new ProjectMemberId(user.getId(), project.getId());
    }

    public ProjectMemberId getId() {
        return id;
    }
    public void setId(ProjectMemberId id) {
        this.id = id;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public Project getProject() {
        return project;
    }
    public void setProject(Project project) {
        this.project = project;
    }
    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }
}
