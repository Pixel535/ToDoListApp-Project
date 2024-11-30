package com.example.todoapp.Model;

public class PaymentResponse {

    private String redirectUrl;
    private String message;
    private boolean success;
    private String userId;

    public PaymentResponse() {}

    public PaymentResponse(String redirectUrl, String message, boolean success, String userId) {
        this.redirectUrl = redirectUrl;
        this.message = message;
        this.success = success;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
