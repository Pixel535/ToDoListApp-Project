package com.example.todoapp;

import android.app.Activity;
import android.os.Bundle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.todoapp.Model.Order;
import com.example.todoapp.Network.PayPalService;
import com.example.todoapp.Network.RetrofitClient;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends Activity {

    private PayPalService paymentService;
    private String jwt;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        jwt = preferences.getString("jwt", "");
        userId = preferences.getString("userId", "");

        paymentService = RetrofitClient.getRetrofitInstance().create(PayPalService.class);

        Button payButton = findViewById(R.id.payButton);
        Button goBackButton = findViewById(R.id.goBackButton);

        payButton.setOnClickListener(v -> initiatePayment());
        goBackButton.setOnClickListener(v -> finish());
    }

    private void initiatePayment() {
        Order order = new Order();
        order.setAmount(5.0);
        order.setCurrency("USD");
        order.setMethod("paypal");
        order.setIntent("Sale");
        order.setDescription("Payment for Premium Features");

        Call<Map<String, String>> call = paymentService.createPayment(jwt, userId, order);
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String redirectUrl = response.body().get("redirectUrl");
                    if (redirectUrl != null) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirectUrl));
                        startActivity(browserIntent);
                    } else {
                        Toast.makeText(PaymentActivity.this, "Failed to get redirect URL", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("PaymentActivity", "Payment initiation failed: " + response.message());
                    Toast.makeText(PaymentActivity.this, "Payment initiation failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Log.e("PaymentActivity", "Error initiating payment", t);
                Toast.makeText(PaymentActivity.this, "Payment initiation error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
