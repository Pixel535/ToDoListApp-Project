package com.example.todoapp.Model;

import java.time.LocalDate;
import java.util.Date;

public class Task {

    private String id;
    private String title;
    private String dueDate;
    private boolean completed;
    private String userId;

    public Task() {}

    public Task(String title, String dueDate, boolean completed, String userId) {
        this.id = id;
        this.title = title;
        this.dueDate = dueDate;
        this.completed = completed;
        this.userId = userId;
    }

    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getDueDate() { return dueDate; }

    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
}
