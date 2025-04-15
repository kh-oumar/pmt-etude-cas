package com.codesolutions.pmt_backend.repository;

import com.codesolutions.pmt_backend.entity.Invitation;
import com.codesolutions.pmt_backend.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    // Recherche d'une invitation par token
    Optional<Invitation> findByToken(String token);

    // Récupérer les invitations associées à une adresse email donnée
    List<Invitation> findByEmail(String email);

    Optional<Invitation> findByProjectAndEmail(Project project, String email);
}
