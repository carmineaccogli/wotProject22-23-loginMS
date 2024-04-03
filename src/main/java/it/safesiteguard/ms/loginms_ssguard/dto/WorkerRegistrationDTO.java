package it.safesiteguard.ms.loginms_ssguard.dto;

import it.safesiteguard.ms.loginms_ssguard.domain.User;

public class WorkerRegistrationDTO {

    private String email;

    private User.Role role;

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
