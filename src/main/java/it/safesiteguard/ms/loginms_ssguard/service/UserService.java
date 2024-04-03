package it.safesiteguard.ms.loginms_ssguard.service;

import it.safesiteguard.ms.loginms_ssguard.domain.User;
import it.safesiteguard.ms.loginms_ssguard.exceptions.UserNotFoundException;
import org.springframework.mail.MailException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserService {

    User addUser(User newUser);

    List<User> getAll();
    String createJwtToken(User user, String userAgent) throws UsernameNotFoundException;

    String addWorkerUser(String email, User.Role role) throws MailException;

    User getUserByID(String userID) throws UserNotFoundException;

    List<User> getMacAddressesByUsersList(List<String> usersIDs);

    List<User> getAllDriversMacAddresses();
}
