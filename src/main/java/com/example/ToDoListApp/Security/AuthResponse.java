package com.example.ToDoListApp.Security;


public class AuthResponse {
    private String jwt;
    private String message;
    private Boolean status;
    private String userId;

    public AuthResponse(String jwt, String message, Boolean status, String userId) {
        this.jwt = jwt;
        this.message = message;
        this.status = status;
        this.userId = userId;
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
