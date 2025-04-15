package com.codesolutions.pmt_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "invitation")
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Le projet auquel est associée l'invitation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Project project;

    // Adresse email de l'invité
    @Column(nullable = false)
    private String email;

    // Token unique de l'invitation
    @Column(nullable = false, length = 50)
    private String token;

    // Référence à l'utilisateur qui a envoyé l'invitation (souvent l'admin)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User invitedBy;

    // Statut de l'invitation (ex: "en attente", "acceptée", etc.)
    @Column(length = 20)
    private String status;

    public Invitation() {
    }

    public Invitation(Project project, String email, String token, User invitedBy, String status) {
        this.project = project;
        this.email = email;
        this.token = token;
        this.invitedBy = invitedBy;
        this.status = status;
    }

    // Getters et setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Project getProject() {
        return project;
    }
    public void setProject(Project project) {
        this.project = project;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public User getInvitedBy() {
        return invitedBy;
    }
    public void setInvitedBy(User invitedBy) {
        this.invitedBy = invitedBy;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
