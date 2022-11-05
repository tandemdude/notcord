package io.github.tandemdude.notcord.authorizer.components;

import io.github.tandemdude.notcord.authorizer.config.EmailProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class EmailSender {
    private final EmailProperties emailProperties;
    private final JavaMailSender javaMailSender;
    private final ExecutorService executorService;

    public EmailSender(EmailProperties emailProperties, JavaMailSender javaMailSender) {
        this.emailProperties = emailProperties;
        this.javaMailSender = javaMailSender;
        this.executorService = Executors.newCachedThreadPool();
    }

    public void sendEmailAsync(String recipient, String subject, String content) {
        executorService.submit(() -> {
            var message = new SimpleMailMessage();
            message.setFrom(emailProperties.getAddress());
            message.setTo(recipient);
            message.setSubject(subject);
            message.setText(content);
            javaMailSender.send(message);
        });
    }
}
