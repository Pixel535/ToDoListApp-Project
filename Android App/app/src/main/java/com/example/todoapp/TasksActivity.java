package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.todoapp.Adapter.TaskAdapter;
import com.example.todoapp.Model.Task;
import com.example.todoapp.Network.RetrofitClient;
import com.example.todoapp.Network.TaskService;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TasksActivity extends Activity implements TaskAdapter.TaskActionsListener {

    private List<Task> tasks = new ArrayList<>();
    private TaskAdapter taskAdapter;
    private TaskService taskService;
    private String jwt;
    private String userId;
    private String username;
    private boolean isPremium;
    private TextView emptyListMessage;
    private RecyclerView tasksRecyclerView;
    private Button payForDarkModeButton;
    private Button toggleDarkModeButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isDarkModeEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        jwt = preferences.getString("jwt", "");
        userId = preferences.getString("userId", "");
        username = preferences.getString("username", "");
        isPremium = preferences.getBoolean("premiumUser", false);

        taskService = RetrofitClient.getRetrofitInstance().create(TaskService.class);

        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(tasks, this, isDarkModeEnabled);
        tasksRecyclerView.setAdapter(taskAdapter);

        emptyListMessage = findViewById(R.id.emptyListMessage);

        payForDarkModeButton = findViewById(R.id.payForDarkModeButton);
        toggleDarkModeButton = findViewById(R.id.toggleDarkModeButton);

        if (isPremium) {
            toggleDarkModeButton.setVisibility(View.VISIBLE);
            toggleDarkModeButton.setOnClickListener(v -> toggleDarkMode());
        } else {
            payForDarkModeButton.setVisibility(View.VISIBLE);
            payForDarkModeButton.setOnClickListener(v -> startActivity(new Intent(TasksActivity.this, PaymentActivity.class)));
        }

        Button addTaskButton = findViewById(R.id.addTaskButton);
        addTaskButton.setOnClickListener(v -> addNewTask());

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> logout());

        TextView userGreeting = findViewById(R.id.userGreeting);
        String premiumStatus = isPremium ? "Yes" : "No";
        String greetingText = "Hello, " + username + "! Are you premium [" + premiumStatus + "]";
        userGreeting.setText(greetingText);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadTasks();
            swipeRefreshLayout.setRefreshing(false);
        });


        loadTasks();
    }

    private void toggleDarkMode() {
        String currentText = toggleDarkModeButton.getText().toString();
        ConstraintLayout rootLayout = findViewById(R.id.rootLayout);
        TextView tasksHeader = findViewById(R.id.tasksHeader);
        TextView userGreeting = findViewById(R.id.userGreeting);
        RecyclerView tasksRecyclerView = findViewById(R.id.tasksRecyclerView);

        if (currentText.equals("Switch to Dark Mode")) {
            isDarkModeEnabled = true;
            toggleDarkModeButton.setText("Switch to Light Mode");
        } else {
            isDarkModeEnabled = false;
            toggleDarkModeButton.setText("Switch to Dark Mode");
        }

        int backgroundColor = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            backgroundColor = getResources().getColor(isDarkModeEnabled  ? R.color.dark_background_color : R.color.light_background_color, getTheme());
        }
        int textColor = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textColor = getResources().getColor(isDarkModeEnabled  ? R.color.dark_text_color : R.color.light_text_color, getTheme());
        }

        rootLayout.setBackgroundColor(backgroundColor);
        userGreeting.setTextColor(textColor);
        tasksHeader.setTextColor(textColor);
        tasksRecyclerView.setBackgroundColor(backgroundColor);

        taskAdapter.setDarkModeEnabled(isDarkModeEnabled);
    }

    private void loadTasks() {
        Call<List<Task>> call = taskService.getAllTasks(jwt, userId);
        call.enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tasks.clear();
                    tasks.addAll(response.body());
                    taskAdapter.notifyDataSetChanged();
                    taskAdapter.setDarkModeEnabled(isDarkModeEnabled);
                    updateEmptyListMessage();
                    Log.d("TasksActivity", "Tasks loaded successfully: " + tasks.size());
                } else {
                    Log.e("TasksActivity", "Error loading tasks: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                Log.e("TasksActivity", "Failed to load tasks", t);
            }
        });
    }

    private void addNewTask() {
        Task newTask = new Task("New Task", "", false, userId);
        Call<Task> call = taskService.createTask(jwt, userId, newTask);
        call.enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tasks.add(response.body());
                    taskAdapter.notifyDataSetChanged();
                    taskAdapter.setDarkModeEnabled(isDarkModeEnabled);
                    updateEmptyListMessage();
                    Log.d("TasksActivity", "Task added: " + response.body().getTitle());
                } else {
                    Log.e("TasksActivity", "Failed to add task: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                Log.e("TasksActivity", "Failed to add task", t);
            }
        });
    }

    private void logout() {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        preferences.edit().clear().apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void onUpdateTask(Task task) {
        Call<Task> call = taskService.updateTask(jwt, userId, task.getId(), task);
        call.enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int index = tasks.indexOf(task);
                    if (index >= 0) {
                        tasks.set(index, response.body());
                        taskAdapter.notifyItemChanged(index);
                        taskAdapter.setDarkModeEnabled(isDarkModeEnabled);
                        Log.d("TasksActivity", "Task updated: " + response.body().getTitle());
                    }
                } else {
                    Log.e("TasksActivity", "Failed to update task: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                Log.e("TasksActivity", "Failed to update task", t);
            }
        });
    }

    @Override
    public void onDeleteTask(Task task) {
        Call<Void> call = taskService.deleteTask(jwt, userId, task.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    tasks.remove(task);
                    taskAdapter.notifyDataSetChanged();
                    taskAdapter.setDarkModeEnabled(isDarkModeEnabled);
                    updateEmptyListMessage();
                    Log.d("TasksActivity", "Task deleted: " + task.getTitle());
                } else {
                    Log.e("TasksActivity", "Failed to delete task: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("TasksActivity", "Failed to delete task", t);
            }
        });
    }

    private void updateEmptyListMessage() {
        if (tasks.isEmpty()) {
            emptyListMessage.setVisibility(View.VISIBLE);
            tasksRecyclerView.setVisibility(View.GONE);
        } else {
            emptyListMessage.setVisibility(View.GONE);
            tasksRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}