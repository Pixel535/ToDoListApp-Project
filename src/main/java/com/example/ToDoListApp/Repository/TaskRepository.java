package com.example.ToDoListApp.Repository;
import com.example.ToDoListApp.Model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskRepository extends MongoRepository<Task, String> {
}
