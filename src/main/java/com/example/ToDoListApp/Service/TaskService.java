package com.example.ToDoListApp.Service;
import com.example.ToDoListApp.Model.Task;
import com.example.ToDoListApp.Repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository repository;

    public List<Task> getAllTasks() {
        return repository.findAll();
    }

    public Optional<Task> getTaskById(String id) {
        return repository.findById(id);
    }

    public Task saveTask(Task task) {
        return repository.save(task);
    }

    public void deleteTask(String id) {
        repository.deleteById(id);
    }
}
