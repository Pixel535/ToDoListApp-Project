package com.example.ToDoListApp.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @DocumentReference
    private List<Task> tasks;

    public User() {}

    public User(String username, String id, String password, List<Task> tasks) {
        this.username = username;
        this.id = id;
        this.password = password;
        this.tasks = tasks;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public List<Task> getTasks() { return tasks; }

    public void setTasks(List<Task> tasks) { this.tasks = tasks; }
}
