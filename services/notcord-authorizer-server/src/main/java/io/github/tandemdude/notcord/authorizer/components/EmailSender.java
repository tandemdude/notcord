package io.github.tandemdude.notcord.authorizer.components;

import io.github.tandemdude.notcord.authorizer.config.EmailConfig;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class EmailSender {
    private final EmailConfig emailConfig;
    private final JavaMailSender javaMailSender;
    private final ExecutorService executorService;

    public EmailSender(EmailConfig emailConfig, JavaMailSender javaMailSender) {
        this.emailConfig = emailConfig;
        this.javaMailSender = javaMailSender;
        this.executorService = Executors.newCachedThreadPool();
    }

    public void sendEmailAsync(String recipient, String subject, String content) {
        executorService.submit(() -> {
            var message = new SimpleMailMessage();
            message.setFrom(emailConfig.getAddress());
            message.setTo(recipient);
            message.setSubject(subject);
            message.setText(content);
            javaMailSender.send(message);
        });
    }
}
