package com.example.ToDoListApp.Security;


public class AuthResponse {
    private String jwt;
    private String message;
    private Boolean status;
    private String userId;
    private boolean premiumUser;

    public AuthResponse(String jwt, String message, Boolean status, String userId, boolean premiumUser) {
        this.jwt = jwt;
        this.message = message;
        this.status = status;
        this.userId = userId;
        this.premiumUser = premiumUser;
    }

    public boolean isPremiumUser() {
        return premiumUser;
    }

    public void setPremiumUser(boolean premiumUser) {
        this.premiumUser = premiumUser;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
