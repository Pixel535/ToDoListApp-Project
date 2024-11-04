package com.example.todoapp.Network;

import com.example.todoapp.Model.AuthResponse;
import com.example.todoapp.Model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {
    @POST("auth/signup")
    Call<AuthResponse> signup(@Body User user);

    @POST("auth/signin")
    Call<AuthResponse> signin(@Body User loginRequest);
}

