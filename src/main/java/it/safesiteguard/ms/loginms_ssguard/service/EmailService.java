package it.safesiteguard.ms.loginms_ssguard.service;

import org.springframework.mail.MailException;

public interface EmailService {

    void sendCredentialsEmail(String recipient, String username, String password) throws MailException;
}
