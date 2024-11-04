package com.example.ToDoListApp.Controller;

import com.example.ToDoListApp.Model.Order;
import com.example.ToDoListApp.Model.User;
import com.example.ToDoListApp.Paypal.PaymentResponse;
import com.example.ToDoListApp.Repository.UserRepository;
import com.example.ToDoListApp.Service.PayPalService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/android/payment")
public class AndroidPayPalController {

    @Autowired
    private PayPalService paypalService;

    @Autowired
    private UserRepository userRepository;

    private static final String SUCCESS_URL = "myapp://api/android/payment/pay/success";
    private static final String CANCEL_URL = "myapp://api/android/payment/pay/cancel";

    @PostMapping("/pay")
    public ResponseEntity<Map<String, String>> payment(@RequestBody Order order, @RequestHeader("userId") String userId) {
        try {
            Payment payment = paypalService.createPayment(order.getAmount(), order.getCurrency(), order.getMethod(), order.getIntent(), order.getDescription(), CANCEL_URL + "?userId=" + userId, SUCCESS_URL + "?userId=" + userId);
            for(Links link:payment.getLinks()) {
                if(link.getRel().equals("approval_url")) {
                    Map<String, String> response = new HashMap<>();
                    response.put("redirectUrl", link.getHref());
                    return ResponseEntity.ok(response);
                }
            }
        } catch (PayPalRESTException e) {
            throw new RuntimeException(e);
        }
        Map<String, String> response = new HashMap<>();
        response.put("redirectUrl", "/paymentInfo");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pay/cancel")
    public ResponseEntity<Map<String, String>> cancelPay(@RequestParam("userId") String userId) throws IOException {
        if (userId == null) {
            return ResponseEntity.ok(Map.of("message", "Error finding user", "status", "canceled"));
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setPremiumUser(false);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Payment canceled", "status", "canceled"));
    }

    @GetMapping("/pay/success")
    public ResponseEntity<Map<String, String>> successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId, @RequestParam("userId") String userId) throws IOException {
        if (userId == null) {
            return ResponseEntity.ok(Map.of("message", "Error finding user", "status", "canceled"));
        }
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
                user.setPremiumUser(true);
                userRepository.save(user);
                return ResponseEntity.ok(Map.of("message", "Payment successful", "status", "success"));
            }
        } catch (PayPalRESTException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(Map.of("message", "Payment not approved", "status", "error"));
    }

}
