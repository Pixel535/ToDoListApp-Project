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
import com.example.todoapp.Network.RetrofitClient;
import com.example.todoapp.Network.UserService;
import com.example.todoapp.Model.AuthResponse;
import com.example.todoapp.Model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends Activity {

    private EditText usernameEditText, passwordEditText, confirmPasswordEditText;
    private TextView errorTextView;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        errorTextView = findViewById(R.id.errorTextView);
        Button signupButton = findViewById(R.id.signupButton);

        userService = RetrofitClient.getRetrofitInstance().create(UserService.class);

        signupButton.setOnClickListener(view -> handleSignup());
    }

    private void handleSignup() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorTextView.setText("Please fill in all fields.");
            errorTextView.setVisibility(View.VISIBLE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorTextView.setText("Passwords do not match.");
            errorTextView.setVisibility(View.VISIBLE);
            return;
        }

        Call<AuthResponse> call = userService.signup(new User(username, password));
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    saveUserCredentials(authResponse);

                    Intent intent = new Intent(SignUpActivity.this, TasksActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    errorTextView.setText("Signup failed. Try again.");
                    errorTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "Signup failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
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
