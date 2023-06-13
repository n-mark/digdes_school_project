package ru.digdes.school.email;

import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class EmailListener {
    private final EmailSender emailSender;

    @Autowired
    public EmailListener(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    @RabbitListener(queues = "${rabbitmq.queue}")
    public void processEmail(EmailMessage emailMessage) {
        log.info("EmailListener.processEmail invoked with " + emailMessage);
        try {
            emailSender.sendEmail(emailMessage);
        } catch (MailException | MessagingException | TemplateException | IOException e) {
            log.error("An exception occurred while trying to send email " + e.getMessage());
        }
    }
}
