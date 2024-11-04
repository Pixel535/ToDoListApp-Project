package com.example.todoapp.Network;

import com.example.todoapp.Model.Task;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TaskService {

    @GET("api/tasks")
    Call<List<Task>> getAllTasks(@Header("Authorization") String jwt, @Header("userId") String userId);

    @POST("api/tasks")
    Call<Task> createTask(@Header("Authorization") String jwt, @Header("userId") String userId, @Body Task task);

    @PUT("api/tasks/{taskId}")
    Call<Task> updateTask(@Header("Authorization") String jwt, @Header("userId") String userId,
                          @Path("taskId") String taskId, @Body Task task);

    @DELETE("api/tasks/{taskId}")
    Call<Void> deleteTask(@Header("Authorization") String jwt, @Header("userId") String userId, @Path("taskId") String taskId);
}
