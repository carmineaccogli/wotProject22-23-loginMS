package it.safesiteguard.ms.loginms_ssguard.mappers;

import it.safesiteguard.ms.loginms_ssguard.domain.User;
import it.safesiteguard.ms.loginms_ssguard.dto.AuthorizedOperatorDTO;
import it.safesiteguard.ms.loginms_ssguard.dto.LoginDTO;
import it.safesiteguard.ms.loginms_ssguard.dto.RegistrationRequestDTO;
import org.springframework.stereotype.Component;

import static it.safesiteguard.ms.loginms_ssguard.configuration.SecurityConfig.passwordEncoder;

@Component
public class UserMapper {

    public User fromRegistrationDTOToUser(RegistrationRequestDTO requestDTO) {

        User newUser = new User();

        newUser.setUsername(requestDTO.getUsername());
        newUser.setPassword(passwordEncoder().encode(requestDTO.getPassword()));
        newUser.setRole(requestDTO.getRole());
        newUser.setEmail(requestDTO.getEmail());
        return newUser;
    }

    public LoginDTO fromUserToLoginDTO(User user) {

        LoginDTO loginDTO = new LoginDTO();

        loginDTO.setId(user.getId());
        loginDTO.setUsername(user.getUsername());
        loginDTO.setEmail(user.getEmail());
        loginDTO.setRole(user.getRole());

        if(user.getRole().equals(User.Role.EQUIPMENT_OPERATOR))
            loginDTO.setMacAddress(user.getMacAddress());

        return loginDTO;
    }

    public User fromLoginDTOToUser(LoginDTO loginDTO) {

        User newUser = new User();

        newUser.setId(loginDTO.getId());
        newUser.setUsername(loginDTO.getUsername());
        newUser.setEmail(loginDTO.getEmail());
        newUser.setRole(loginDTO.getRole());
        newUser.setPassword(loginDTO.getPassword());

        if(loginDTO.getMacAddress() != null)
            newUser.setMacAddress(loginDTO.getMacAddress());

        return newUser;
    }

    public AuthorizedOperatorDTO fromUserToAuthorizedOperatorDTO(User user) {
        AuthorizedOperatorDTO authorizedOperatorDTO = new AuthorizedOperatorDTO();

        authorizedOperatorDTO.setUserID(user.getId());
        authorizedOperatorDTO.setMacAddress(user.getMacAddress());

        return authorizedOperatorDTO;
    }

}
