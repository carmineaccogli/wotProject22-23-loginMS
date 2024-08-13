package it.safesiteguard.ms.loginms_ssguard.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService{

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SimpleMailMessage templateCredentialsMessage;


    public void sendCredentialsEmail(String recipient, String username, String password) throws MailException {

        SimpleMailMessage message = new SimpleMailMessage(templateCredentialsMessage);

        // Setting del messaggio da inviare
        String text = String.format(templateCredentialsMessage.getText(), username, password);
        message.setText(text);
        message.setTo("carmine.accogli01@gmail.com"); // recipient

        // Invio della mail
        mailSender.send(message);

    }
}
