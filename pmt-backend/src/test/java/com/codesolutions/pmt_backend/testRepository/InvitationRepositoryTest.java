package com.codesolutions.pmt_backend.testRepository;

import com.codesolutions.pmt_backend.entity.Invitation;
import com.codesolutions.pmt_backend.entity.Project;
import com.codesolutions.pmt_backend.entity.User;
import com.codesolutions.pmt_backend.repository.InvitationRepository;
import com.codesolutions.pmt_backend.repository.ProjectRepository;
import com.codesolutions.pmt_backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class InvitationRepositoryTest {

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testCreateAndFindInvitation() {
        // Création d'un projet et d'un utilisateur qui envoie l'invitation
        Project project = new Project("Projet Invitation", "Test invitation", LocalDate.now());
        project = projectRepository.save(project);
        assertNotNull(project.getId());

        User inviter = new User("inviteur", "inviteur@example.com", "password");
        inviter = userRepository.save(inviter);
        assertNotNull(inviter.getId());

        // Création d'une invitation
        Invitation invitation = new Invitation(project, "invitee@example.com", "token123", inviter, "en attente");
        Invitation savedInvitation = invitationRepository.save(invitation);
        assertNotNull(savedInvitation.getId());

        // Recherche par token
        Invitation invitationByToken = invitationRepository.findByToken("token123").orElse(null);
        assertNotNull(invitationByToken, "L'invitation doit être retrouvée par son token");

        // Recherche par email
        List<Invitation> invitationsByEmail = invitationRepository.findByEmail("invitee@example.com");
        assertFalse(invitationsByEmail.isEmpty(), "Il doit y avoir une invitation pour l'email donné");

        // Recherche par projet et email
        Invitation invitationByProjAndEmail = invitationRepository.findByProjectAndEmail(project, "invitee@example.com").orElse(null);
        assertNotNull(invitationByProjAndEmail, "L'invitation doit être retrouvée par projet et email");
    }
}
