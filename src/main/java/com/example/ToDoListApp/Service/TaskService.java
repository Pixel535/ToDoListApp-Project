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
    private TaskRepository taskRepository;

    public List<Task> getAllTasks(String userId) {
        return taskRepository.findByUserId(userId);
    }

    public Optional<Task> getTaskById(String id) {
        return taskRepository.findById(id);
    }

    public Task saveTask(Task task, String userId) {
        task.setUserId(userId);
        return taskRepository.save(task);
    }

    public void deleteTask(String taskId, String userId) {
        Optional<Task> task = taskRepository.findById(taskId);
        if (task.isPresent() && task.get().getUserId().equals(userId)) {
            taskRepository.delete(task.get());
        } else {
            throw new RuntimeException("Task not found or access denied");
        }
    }

    public Task updateTask(Task updatedTask, String userId) {
        Optional<Task> taskOptional = taskRepository.findById(updatedTask.getId());
        if (taskOptional.isPresent() && taskOptional.get().getUserId().equals(userId)) {
            updatedTask.setUserId(userId); // Ensure the task retains its userId
            return taskRepository.save(updatedTask);
        } else {
            throw new RuntimeException("Task not found or access denied");
        }
    }
}
