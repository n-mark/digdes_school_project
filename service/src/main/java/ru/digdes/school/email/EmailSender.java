package ru.digdes.school.email;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class EmailSender {
    private final JavaMailSender javaMailSender;
    private final Configuration configuration;
    @Value("${spring.mail.username}")
    private String sender;

    public EmailSender(JavaMailSender javaMailSender,
                       Configuration configuration) {
        this.javaMailSender = javaMailSender;
        this.configuration = configuration;
    }

    public void sendEmail(EmailMessage emailMessage) throws MailException, MessagingException, TemplateException, IOException {
        log.info("EmailSender.send email has been started");

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setFrom(sender);
        helper.setSubject(emailMessage.getSubject());
        helper.setTo(emailMessage.getEmail());
        String emailContent = getEmailContent(emailMessage);
        helper.setText(emailContent, true);
        javaMailSender.send(mimeMessage);

        log.info("EmailSender.send email has been finished");
    }

    String getEmailContent(EmailMessage emailMessage) throws IOException, TemplateException {
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        model.put("emailMessage", emailMessage);
        configuration.getTemplate("email.ftlh").process(model, stringWriter);
        return stringWriter.getBuffer().toString();
    }
}

