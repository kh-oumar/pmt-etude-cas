package com.codesolutions.pmt_backend.testController;

import com.codesolutions.pmt_backend.controller.InvitationController;
import com.codesolutions.pmt_backend.entity.Invitation;
import com.codesolutions.pmt_backend.service.InvitationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean; // Note : déprécié depuis Spring Boot 3.4, mais utilisable pour l'instant
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InvitationController.class)
public class InvitationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InvitationService invitationService;

    @Test
    public void testInviteMember() throws Exception {
        Invitation invitationRequest = new Invitation();
        invitationRequest.setEmail("invitee@example.com");

        Invitation savedInvitation = new Invitation();
        savedInvitation.setId(1L);
        savedInvitation.setEmail("invitee@example.com");
        savedInvitation.setToken("token123");
        savedInvitation.setStatus("en attente");

        when(invitationService.inviteMember(eq(1L), eq("invitee@example.com"), eq(2L)))
                .thenReturn(savedInvitation);

        mockMvc.perform(post("/api/invitations?projectId=1&inviterId=2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invitationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("invitee@example.com"))
                .andExpect(jsonPath("$.token").value("token123"))
                .andExpect(jsonPath("$.status").value("en attente"));
    }
}
