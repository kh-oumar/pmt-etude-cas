package com.codesolutions.pmt_backend.service;

import com.codesolutions.pmt_backend.entity.Invitation;
import com.codesolutions.pmt_backend.entity.Project;
import com.codesolutions.pmt_backend.entity.ProjectMember;
import com.codesolutions.pmt_backend.entity.ProjectMemberId;
import com.codesolutions.pmt_backend.entity.Role;
import com.codesolutions.pmt_backend.entity.User;
import com.codesolutions.pmt_backend.repository.InvitationRepository;
import com.codesolutions.pmt_backend.repository.ProjectMemberRepository;
import com.codesolutions.pmt_backend.repository.ProjectRepository;
import com.codesolutions.pmt_backend.repository.RoleRepository;
import com.codesolutions.pmt_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final RoleRepository roleRepository;

    public InvitationService(InvitationRepository invitationRepository,
                             ProjectRepository projectRepository,
                             UserRepository userRepository,
                             ProjectMemberRepository projectMemberRepository,
                             RoleRepository roleRepository) {
        this.invitationRepository = invitationRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Crée une invitation pour un projet, après vérification de l'existence d'un utilisateur avec cet email.
     */
    @Transactional
    public Invitation inviteMember(Long projectId, String email, Long inviterId) {
        // Récupération du projet
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Projet non trouvé"));

        // Récupération de l'utilisateur qui invite
        User inviter = userRepository.findById(inviterId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur invitant non trouvé"));

        // Vérifier l'existence de l'utilisateur à inviter
        User invitedUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Aucun utilisateur trouvé avec l'email " + email));

        // Génération d'un token unique pour l'invitation
        String token = UUID.randomUUID().toString();

        // Création de l'invitation avec le statut "en attente"
        Invitation invitation = new Invitation(project, email, token, inviter, "en attente");
        Invitation savedInvitation = invitationRepository.save(invitation);

        // Ici, vous pouvez ajouter la logique pour envoyer l'e-mail d'invitation avec le token.

        return savedInvitation;
    }

    /**
     * Valide une invitation par token, met à jour son statut et crée l'association dans project_member
     * si l'utilisateur correspondant existe.
     */
    @Transactional
    public Invitation acceptInvitation(String token) {
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invitation invalide"));

        invitation.setStatus("acceptée");
        Invitation savedInvitation = invitationRepository.save(invitation);

        // Vérifier si un utilisateur avec cet email existe
        Optional<User> optionalUser = userRepository.findByEmail(invitation.getEmail());
        if (optionalUser.isPresent()) {
            User invitedUser = optionalUser.get();
            // Récupérer le rôle par défaut à attribuer
            Role defaultRole = roleRepository.findByName("OBSERVER")
                    .orElseThrow(() -> new IllegalArgumentException("Rôle OBSERVER non trouvé"));

            // Créer l'association ProjectMember et l'enregistrer
            Project project = invitation.getProject();
            ProjectMember projectMember = new ProjectMember(invitedUser, project, defaultRole);
            projectMemberRepository.save(projectMember);
        }

        return savedInvitation;
    }
}
