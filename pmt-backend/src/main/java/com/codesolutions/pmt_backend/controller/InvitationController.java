package com.codesolutions.pmt_backend.controller;

import com.codesolutions.pmt_backend.entity.Invitation;
import com.codesolutions.pmt_backend.service.InvitationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invitations")
public class InvitationController {

    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    /**
     * Endpoint pour inviter un membre à un projet.
     * Exemple d'appel : POST /api/invitations?projectId=1&inviterId=2 avec body { "email": "invite@example.com" }
     */
    @PostMapping
    public ResponseEntity<Invitation> inviteMember(@RequestParam Long projectId,
                                                   @RequestParam Long inviterId,
                                                   @RequestBody Invitation invitationRequest) {
        Invitation invitation = invitationService.inviteMember(projectId, invitationRequest.getEmail(), inviterId);
        return ResponseEntity.status(HttpStatus.CREATED).body(invitation);
    }

    /**
     * Endpoint pour accepter une invitation par token.
     */
    @PostMapping("/accept")
    public ResponseEntity<Invitation> acceptInvitation(@RequestParam String token) {
        Invitation invitation = invitationService.acceptInvitation(token);
        return ResponseEntity.ok(invitation);
    }

}
