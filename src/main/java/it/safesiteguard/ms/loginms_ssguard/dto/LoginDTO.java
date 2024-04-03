package it.safesiteguard.ms.loginms_ssguard.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.safesiteguard.ms.loginms_ssguard.domain.User;
import jakarta.validation.constraints.Pattern;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginDTO {

    private String id;

    private String username;
    private String email;
    private User.Role role;

    private String password;

    // Campo opzionale valido per l'equipment_operator role
    @Pattern(regexp = "^(([0-9A-Fa-f]{2}:){5}([0-9A-Fa-f]{2}))$", message = "Mac address format not valid")
    private String macAddress;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public User.Role getRole() {
        return role;
    }

    public void setRole(User.Role role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
}
