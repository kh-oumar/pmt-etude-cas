package com.codesolutions.pmt_backend.testService;

import com.codesolutions.pmt_backend.entity.Project;
import com.codesolutions.pmt_backend.service.NotificationService;
import com.codesolutions.pmt_backend.entity.Task;
import com.codesolutions.pmt_backend.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationService service;

    @Test
    void sendAssignmentNotification_sendsProperEmail() {
        // Prépare un user et une task avec projet
        User user = new User();
        user.setEmail("foo@example.com");
        user.setUsername("Alice");

        Project project = new Project();
        project.setName("MonProjet");

        Task task = new Task();
        task.setName("Tâche1");
        task.setProject(project);

        // Appel de la méthode
        service.sendAssignmentNotification(user, task);

        // Vérifie qu'on a bien appelé mailSender.send(...) avec un SimpleMailMessage correct
        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage msg = captor.getValue();
        // Destinataire
        assertThat(msg.getTo()).containsExactly("foo@example.com");
        // Sujet
        assertThat(msg.getSubject())
                .isEqualTo("Nouvelle tâche assignée : Tâche1");
        // Corps du message
        String text = msg.getText();
        assertThat(text).contains("Bonjour Alice");
        assertThat(text).contains("vous a été assignée dans le projet MonProjet");
    }
}

