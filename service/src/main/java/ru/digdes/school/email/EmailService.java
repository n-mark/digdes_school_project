package ru.digdes.school.email;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public EmailService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendEmailAsync(EmailMessage emailMessage) {
        rabbitTemplate.convertAndSend("q.email-sending", emailMessage);
    }
}
