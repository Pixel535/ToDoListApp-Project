package com.example.ToDoListApp.Service;

import com.example.ToDoListApp.Model.User;

import java.util.List;


public interface UserService {


    public List<User> getAllUsers();

    public User findUserProfileByJwt(String jwt);

    public User findUserByUsername(String username);

    public User findUserById(String userId);


}
