package com.example.todoapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todoapp.Network.PayPalService;
import com.example.todoapp.Network.RetrofitClient;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentStatusActivity extends Activity {

    private PayPalService paymentService;
    private String jwt;
    private String userId;
    private String paymentId;
    private String payerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_status);

        paymentService = RetrofitClient.getRetrofitInstance().create(PayPalService.class);
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        jwt = preferences.getString("jwt", "");
        userId = preferences.getString("userId", "");

        Uri data = getIntent().getData();
        if (data != null) {
            String path = data.getPath();
            paymentId = data.getQueryParameter("paymentId");
            payerId = data.getQueryParameter("PayerID");

            if (path != null) {
                if (path.contains("success")) {
                    handlePaymentSuccess();
                } else if (path.contains("cancel")) {
                    handlePaymentCancel();
                }
            }
        }
    }

    private void handlePaymentSuccess() {
        ((TextView) findViewById(R.id.paymentStatusMessage)).setText("Processing successful payment...");
        Call<Map<String, String>> call = paymentService.successPay(jwt, paymentId, payerId, userId);
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().get("message");
                    String status = response.body().get("status");

                    ((TextView) findViewById(R.id.paymentStatusMessage)).setText(message);

                    if ("success".equals(status)) {
                        SharedPreferences.Editor editor = getSharedPreferences("user_prefs", MODE_PRIVATE).edit();
                        editor.putBoolean("premiumUser", true);
                        editor.apply();
                    }
                } else {
                    Log.e("PaymentStatusActivity", "Failed to process successful payment: " + response.message());
                    Toast.makeText(PaymentStatusActivity.this, "Failed to process successful payment", Toast.LENGTH_SHORT).show();
                }
                navigateToMainActivity();
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Log.e("PaymentStatusActivity", "Error processing successful payment", t);
                Toast.makeText(PaymentStatusActivity.this, "Error processing successful payment", Toast.LENGTH_SHORT).show();
                navigateToMainActivity();
            }
        });
    }

    private void handlePaymentCancel() {
        ((TextView) findViewById(R.id.paymentStatusMessage)).setText("Processing canceled payment...");
        Call<Map<String, String>> call = paymentService.cancelPay(jwt, userId);
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().get("message");
                    ((TextView) findViewById(R.id.paymentStatusMessage)).setText(message);
                } else {
                    Log.e("PaymentStatusActivity", "Failed to process canceled payment: " + response.message());
                    Toast.makeText(PaymentStatusActivity.this, "Failed to process canceled payment", Toast.LENGTH_SHORT).show();
                }
                navigateToMainActivity();
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Log.e("PaymentStatusActivity", "Error processing canceled payment", t);
                Toast.makeText(PaymentStatusActivity.this, "Error processing canceled payment", Toast.LENGTH_SHORT).show();
                navigateToMainActivity();
            }
        });
    }

    private void navigateToMainActivity() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(PaymentStatusActivity.this, MainActivity.class));
            finish();
        }, 5000);
    }
}
