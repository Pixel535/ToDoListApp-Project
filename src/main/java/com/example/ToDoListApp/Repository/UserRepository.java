package com.example.ToDoListApp.Repository;

import com.example.ToDoListApp.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String userName);
}
