package it.safesiteguard.ms.loginms_ssguard.dto;

public class AuthorizedOperatorDTO {

    private String userID;

    private String macAddress;


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
}
