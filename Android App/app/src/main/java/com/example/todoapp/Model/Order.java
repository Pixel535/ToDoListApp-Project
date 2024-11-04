package com.example.todoapp.Model;

public class Order {

    private Double amount;
    private String currency;
    private String method;
    private String intent;
    private String description;

    public Order() {}

    public Order(Double amount, String currency, String method, String intent, String description) {
        this.amount = amount;
        this.currency = currency;
        this.method = method;
        this.intent = intent;
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double price) {
        this.amount = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
