package com.marcuswhocodes.notificationservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class EmailNotification {

    private final JavaMailSender mailSender;

    public EmailNotification(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendNotificationEmail(String email, String subject, String message) throws MessagingException {
        String htmlMessage = createNotificationEmail(message);

        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(htmlMessage, true);
        mailSender.send(msg);
    }


    public String createNotificationEmail(String message) {
        return "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "    <meta charset=\"UTF-8\">"
                + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                + "    <title>Your Notification</title>"
                + "</head>"
                + "<body>"
                + "    <div>"
                + "        <div>"
                + "            <h1>Your Notification</h1>"
                + "        </div>"
                + "        <div>"
                + "            <p style=\"font-size: 18px;\">"
                + "                " + message + ""
                + "            </p>"
                + "        <div>"
                + "            <p style=\"font-size: 14px;\">© " + LocalDate.now().getYear() + " Online Store. All rights reserved.</p>"
                + "        </div>"
                + "    </div>"
                + "</body>"
                + "</html>";
    }


}