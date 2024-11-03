package com.example.ToDoListApp.Controller;

import com.example.ToDoListApp.Model.User;
import com.example.ToDoListApp.Security.AuthResponse;
import com.example.ToDoListApp.Security.JwtProvider;
import com.example.ToDoListApp.Service.UserDetailsServiceImpl;
import com.example.ToDoListApp.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.ToDoListApp.Repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsServiceImpl customUserDetails;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) {
        String username = user.getUsername();
        String password = user.getPassword();

        User existingUser = userRepository.findByUsername(username);
        if (existingUser != null) {
            return new ResponseEntity<>(new AuthResponse(null, "Username already exists", false, existingUser.getId(), existingUser.isPremiumUser()), HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(password));
        user.setPremiumUser(false);
        User savedUser = userRepository.save(user);
        String userId = savedUser.getId();
        boolean isPremiumUser = savedUser.isPremiumUser();

        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = JwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse(token, "Registration successful", true, userId, isPremiumUser);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody User loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        User foundUser = userRepository.findByUsername(username);
        String userId = foundUser.getId();
        boolean isPremiumUser = foundUser.isPremiumUser();

        Authentication authentication = authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = JwtProvider.generateToken(authentication);
        AuthResponse authResponse = new AuthResponse(token, "Login successful", true, userId, isPremiumUser);

        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = customUserDetails.loadUserByUsername(username);

        if (userDetails == null || !passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
