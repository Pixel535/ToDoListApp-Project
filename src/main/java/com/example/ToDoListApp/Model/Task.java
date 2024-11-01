package com.example.ToDoListApp.Model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "tasks")
public class Task {

    @Id
    private String id;
    private String title;
    private LocalDate dueDate;
    private boolean completed;
    private String userId;

    public Task() {}

    public Task(String id, String title, LocalDate dueDate, boolean completed, String userId) {
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

    public LocalDate getDueDate() { return dueDate; }

    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
}
