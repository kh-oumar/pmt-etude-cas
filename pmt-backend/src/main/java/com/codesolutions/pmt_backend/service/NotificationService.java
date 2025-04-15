package com.codesolutions.pmt_backend.service;

import com.codesolutions.pmt_backend.entity.Task;
import com.codesolutions.pmt_backend.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final JavaMailSender mailSender;

    @Autowired
    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendAssignmentNotification(User user, Task task) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Nouvelle tâche assignée : " + task.getName());
        message.setText("Bonjour " + user.getUsername() + ",\n\nLa tâche \"" + task.getName() + "\" vous a été assignée dans le projet " +
                task.getProject().getName() + ".\n\nCordialement,\nVotre équipe PMT");

        mailSender.send(message);
    }
}
