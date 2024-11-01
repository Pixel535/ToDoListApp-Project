package com.example.ToDoListApp.Controller;
import com.example.ToDoListApp.Model.Task;
import com.example.ToDoListApp.Service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    public List<Task> getAllTasks(@RequestHeader("userId") String userId) {
        return taskService.getAllTasks(userId);
    }

    @GetMapping("/{id}")
    public Optional<Task> getTaskById(@PathVariable String id) {
        return taskService.getTaskById(id);
    }

    @PostMapping
    public Task createTask(@RequestBody Task task, @RequestHeader("userId") String userId) {
        return taskService.saveTask(task, userId);
    }

    @PutMapping("/{taskId}")
    public Task updateTask(@PathVariable String taskId, @RequestBody Task task, @RequestHeader("userId") String userId) {
        task.setId(taskId);
        return taskService.updateTask(task, userId);
    }

    @DeleteMapping("/{taskId}")
    public void deleteTask(@PathVariable String taskId, @RequestHeader("userId") String userId) {
        taskService.deleteTask(taskId, userId);
    }
}
