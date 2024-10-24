package com.example.notificationservice.service;

import com.example.notificationservice.model.Notification;
import com.example.notificationservice.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private JavaMailSender mailSender;

    public Notification sendNotification(String recipient, String message, String channel) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setMessage(message);
        notification.setChannel(channel);
        notification.setStatus("PENDING");
        notification.setTimestamp(LocalDateTime.now());

        notification = notificationRepository.save(notification);

        if (channel.equals("email")) {
            try {
                sendEmail(recipient, message);
                notification.setStatus("SENT");
            } catch (Exception e) {
                notification.setStatus("FAILED");
            }
        }

        return notificationRepository.save(notification);
    }

    private void sendEmail(String recipient, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(recipient);
        mailMessage.setSubject("Notificación");
        mailMessage.setText(message);
        mailSender.send(mailMessage);
    }

    public Iterable<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id).orElse(null);
    }
}
