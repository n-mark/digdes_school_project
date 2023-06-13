package ru.digdes.school.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailSender {
    private final JavaMailSender javaMailSender;

    public EmailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(String recipient, String subject, String message) throws MailException {
        log.info("EmailSender.send email has been started");
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("digdes.schoolproj@mail.ru");
        mailMessage.setTo(recipient);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        javaMailSender.send(mailMessage);
        log.info("EmailSender.send email has been finished");
    }
}

