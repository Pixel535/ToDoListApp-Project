package com.example.todoapp.Network;

import com.example.todoapp.Model.Order;
import com.example.todoapp.Model.PaymentResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PayPalService {
    @POST("api/android/payment/pay")
    Call<Map<String, String>> createPayment(@Header("Authorization") String jwt, @Header("userId") String userId, @Body Order order);

    @GET("api/android/payment/pay/cancel")
    Call<Map<String, String>> cancelPay(@Header("Authorization") String jwt, @Query("userId") String userId);

    @GET("api/android/payment/pay/success")
    Call<Map<String, String>> successPay(@Header("Authorization") String jwt, @Query("paymentId") String paymentId, @Query("PayerID") String payerId, @Query("userId") String userId);
}
