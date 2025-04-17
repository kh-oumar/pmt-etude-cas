package com.codesolutions.pmt_backend.testService;

import com.codesolutions.pmt_backend.entity.Invitation;
import com.codesolutions.pmt_backend.entity.Project;
import com.codesolutions.pmt_backend.entity.ProjectMember;
import com.codesolutions.pmt_backend.entity.Role;
import com.codesolutions.pmt_backend.entity.User;
import com.codesolutions.pmt_backend.repository.InvitationRepository;
import com.codesolutions.pmt_backend.repository.ProjectMemberRepository;
import com.codesolutions.pmt_backend.repository.ProjectRepository;
import com.codesolutions.pmt_backend.repository.RoleRepository;
import com.codesolutions.pmt_backend.repository.UserRepository;
import com.codesolutions.pmt_backend.service.InvitationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvitationServiceTest {

    @Mock private InvitationRepository invitationRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProjectMemberRepository projectMemberRepository;
    @Mock private RoleRepository roleRepository;

    @InjectMocks private InvitationService service;

    //--- inviteMember() ---//

    @Test @DisplayName("inviteMember: projet absent → IllegalArgument")
    void inviteMember_projectNotFound_throws() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.inviteMember(1L, "foo@x.com", 10L)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Projet non trouvé");
    }

    @Test @DisplayName("inviteMember: inviter absent → IllegalArgument")
    void inviteMember_inviterNotFound_throws() {
        Project p = new Project(); p.setId(2L);
        when(projectRepository.findById(2L)).thenReturn(Optional.of(p));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.inviteMember(2L, "foo@x.com", 99L)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Utilisateur invitant non trouvé");
    }

    @Test @DisplayName("inviteMember: email inconnu → IllegalArgument")
    void inviteMember_emailNotFound_throws() {
        Project p = new Project(); p.setId(3L);
        when(projectRepository.findById(3L)).thenReturn(Optional.of(p));
        User inviter = new User(); inviter.setId(20L);
        when(userRepository.findById(20L)).thenReturn(Optional.of(inviter));
        when(userRepository.findByEmail("bar@x.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.inviteMember(3L, "bar@x.com", 20L)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Aucun utilisateur trouvé");
    }

    @Test @DisplayName("inviteMember: succès → sauvegarde invitation")
    void inviteMember_success() {
        // given
        Project p = new Project(); p.setId(4L);
        when(projectRepository.findById(4L)).thenReturn(Optional.of(p));

        User inviter = new User(); inviter.setId(30L);
        when(userRepository.findById(30L)).thenReturn(Optional.of(inviter));

        User invited = new User();
        invited.setId(31L);
        invited.setEmail("baz@x.com");
        when(userRepository.findByEmail("baz@x.com")).thenReturn(Optional.of(invited));

        Invitation toSave = new Invitation(p, "baz@x.com", null, inviter, "en attente");
        Invitation saved = new Invitation(p, "baz@x.com", "tok", inviter, "en attente");
        saved.setId(55L);

        // on stub save, on capture le token généré
        when(invitationRepository.save(any(Invitation.class))).thenAnswer(inv -> {
            Invitation invt = inv.getArgument(0);
            // simuler qu'il a mis le token
            invt.setToken("tok");
            invt.setId(55L);
            return invt;
        });

        // when
        Invitation out = service.inviteMember(4L, "baz@x.com", 30L);

        // then
        assertThat(out.getId()).isEqualTo(55L);
        assertThat(out.getStatus()).isEqualTo("en attente");
        assertThat(out.getToken()).isNotEmpty();
        verify(invitationRepository).save(argThat(invt ->
                invt.getProject().equals(p) &&
                        "baz@x.com".equals(invt.getEmail()) &&
                        invt.getInvitedBy().equals(inviter)
        ));
    }

    //--- acceptInvitation() ---//

    @Test @DisplayName("acceptInvitation: token invalide → IllegalArgument")
    void acceptInvitation_invalidToken_throws() {
        when(invitationRepository.findByToken("bad")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.acceptInvitation("bad"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invitation invalide");
    }

    @Test @DisplayName("acceptInvitation: pas d'utilisateur → ne crée pas de ProjectMember")
    void acceptInvitation_userMissing_skipsMember() {
        // setup invitation
        Project proj = new Project(); proj.setId(7L);
        Invitation inv = new Invitation(proj, "noone@x.com", "tok1", null, "en attente");
        when(invitationRepository.findByToken("tok1")).thenReturn(Optional.of(inv));
        when(invitationRepository.save(inv)).thenReturn(inv);
        when(userRepository.findByEmail("noone@x.com")).thenReturn(Optional.empty());

        // when
        Invitation out = service.acceptInvitation("tok1");

        // then
        assertThat(out.getStatus()).isEqualTo("acceptée");
        verify(projectMemberRepository, never()).save(any(ProjectMember.class));
    }

    @Test @DisplayName("acceptInvitation: rôle manquant → IllegalArgument")
    void acceptInvitation_roleNotFound_throws() {
        Project proj = new Project(); proj.setId(8L);
        Invitation inv = new Invitation(proj, "u@x.com", "tok2", null, "en attente");
        when(invitationRepository.findByToken("tok2")).thenReturn(Optional.of(inv));
        when(invitationRepository.save(inv)).thenReturn(inv);
        User u = new User(); u.setEmail("u@x.com");
        when(userRepository.findByEmail("u@x.com")).thenReturn(Optional.of(u));
        when(roleRepository.findByName("OBSERVER")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.acceptInvitation("tok2"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Rôle OBSERVER non trouvé");
    }

    @Test @DisplayName("acceptInvitation: succès → crée ProjectMember")
    void acceptInvitation_success_createsMember() {
        Project proj = new Project(); proj.setId(9L);
        Invitation inv = new Invitation(proj, "v@x.com", "tok3", null, "en attente");
        when(invitationRepository.findByToken("tok3")).thenReturn(Optional.of(inv));
        when(invitationRepository.save(inv)).thenReturn(inv);

        User u = new User(); u.setId(40L); u.setEmail("v@x.com");
        when(userRepository.findByEmail("v@x.com")).thenReturn(Optional.of(u));

        Role r = new Role("OBSERVER");
        when(roleRepository.findByName("OBSERVER")).thenReturn(Optional.of(r));

        // when
        Invitation out = service.acceptInvitation("tok3");

        // then
        assertThat(out.getStatus()).isEqualTo("acceptée");
        verify(projectMemberRepository).save(argThat(pm ->
                pm.getProject().equals(proj) &&
                        pm.getUser().equals(u) &&
                        pm.getRole().equals(r)
        ));
    }
}
