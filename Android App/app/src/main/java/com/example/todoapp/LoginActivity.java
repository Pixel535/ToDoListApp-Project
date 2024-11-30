package com.example.todoapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.todoapp.Model.User;
import com.example.todoapp.Network.RetrofitClient;
import com.example.todoapp.Network.UserService;
import com.example.todoapp.Model.AuthResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends Activity {

    private EditText usernameEditText, passwordEditText;
    private TextView errorTextView;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        errorTextView = findViewById(R.id.errorTextView);
        Button loginButton = findViewById(R.id.loginButton);
        Button goToSignUpButton = findViewById(R.id.goToSignUpButton);

        userService = RetrofitClient.getRetrofitInstance().create(UserService.class);

        loginButton.setOnClickListener(view -> handleLogin());
        goToSignUpButton.setOnClickListener(view -> startActivity(new Intent(this, SignUpActivity.class)));
    }

    private void handleLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorTextView.setText("Please enter both username and password.");
            errorTextView.setVisibility(View.VISIBLE);
            return;
        }

        Call<AuthResponse> call = userService.signin(new User(username, password));
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    saveUserCredentials(authResponse);

                    Intent intent = new Intent(LoginActivity.this, TasksActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    errorTextView.setText("Invalid username or password.");
                    errorTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Login failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveUserCredentials(AuthResponse authResponse) {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("jwt", authResponse.getJwt());
        editor.putString("userId", authResponse.getUserId());
        editor.putString("username", authResponse.getUsername());
        editor.putBoolean("premiumUser", authResponse.isPremiumUser());
        editor.apply();
    }
}
