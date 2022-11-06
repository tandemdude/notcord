/*
 * Copyright 2022 tandemdude
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
