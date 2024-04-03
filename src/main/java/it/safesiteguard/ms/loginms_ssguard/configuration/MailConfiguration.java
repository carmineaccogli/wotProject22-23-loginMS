package it.safesiteguard.ms.loginms_ssguard.configuration;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfiguration {


    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        return mailSender;
    }


    // BEAN per la definizione del template email per comunicare le credenziali di accesso al lavoratore
    @Bean
    public SimpleMailMessage templateCredentialsMessage() {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setText(
                "Hey worker,\n" +
                        "Great news! Your access to the safety system is all set up. \uD83D\uDEE0\uFE0F This tool is crucial for keeping our construction site safe and sound." +
                        "Here are your login details:\n" +
                        "- Operator Code: %s\n" +
                        "- Initial Password: %s\n\n" +
                        "Don't forget to keep these in a safe spot. For security, you might want to change your password the first time you log in.\n\n" +
                        "To get started, grab the app from the Store. Once it's on your phone, punch in your username and password and start to scan.\n"+
                        "Thanks for being part of the crew that's making our construction site a safer place. We appreciate it!");
         message.setSubject("Your SafeSite-Guard Access is Ready");

        return message;
    }

}
