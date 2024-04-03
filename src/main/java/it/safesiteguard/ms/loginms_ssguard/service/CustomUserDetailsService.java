package it.safesiteguard.ms.loginms_ssguard.service;

import it.safesiteguard.ms.loginms_ssguard.domain.User;
import it.safesiteguard.ms.loginms_ssguard.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Optional<User> optUser= userRepository.findByUsername(username);

        if (!optUser.isPresent()) {
            throw new UsernameNotFoundException(username);
        }

        final User user = optUser.get();


        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(user.getUsername()).password(user.getPassword()).roles(user.getRole().toString()).build();

        return userDetails;
    }
}
