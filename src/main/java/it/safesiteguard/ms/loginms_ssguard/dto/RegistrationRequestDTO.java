package it.safesiteguard.ms.loginms_ssguard.dto;

import it.safesiteguard.ms.loginms_ssguard.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RegistrationRequestDTO {


    @NotBlank(message="{NotBlank.regRequest.username}")
    private String username;
    @NotBlank(message="{NotBlank.regRequest.password}")
    private String password;
    @NotNull(message="{NotNull.regRequest.role}")
    private User.Role role;
    @NotBlank(message="{NotBlank.regRequest.email}")
    @Email
    private String email;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User.Role getRole() {
        return role;
    }

    public void setRole(User.Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
