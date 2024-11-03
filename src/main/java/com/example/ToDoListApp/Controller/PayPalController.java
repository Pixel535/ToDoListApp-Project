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

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/payment")
public class PayPalController {

    @Autowired
    private PayPalService paypalService;

    @Autowired
    private UserRepository userRepository;

    private static final String SUCCESS_URL = "http://localhost:8080/api/payment/pay/success";
    private static final String CANCEL_URL = "http://localhost:8080/api/payment/pay/cancel";
    private static final String PAYMENT_STATUS_URL = "http://localhost:3000/paymentStatus";


    @PostMapping("/pay")
    public ResponseEntity<PaymentResponse> payment(@RequestBody Order order, @RequestHeader("userId") String userId) {
        try {
            Payment payment = paypalService.createPayment(order.getAmount(), order.getCurrency(), order.getMethod(), order.getIntent(), order.getDescription(), CANCEL_URL + "?userId=" + userId, SUCCESS_URL + "?userId=" + userId);
            for(Links link:payment.getLinks()) {
                if(link.getRel().equals("approval_url")) {
                    PaymentResponse paymentResponse = new PaymentResponse(link.getHref(), "Payment created successfully", true, userId);
                    return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
                }
            }
        } catch (PayPalRESTException e) {
            throw new RuntimeException(e);
        }
        PaymentResponse paymentResponse = new PaymentResponse("/paymentInfo", "No redirect URL available", false, userId);
        return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
    }

    @GetMapping("/pay/cancel")
    public ResponseEntity<PaymentResponse> cancelPay(@RequestParam("userId") String userId, HttpSession session, HttpServletResponse response) throws IOException {
        if (userId == null) {
            session.setAttribute("paymentStatus", "cancelled");
            session.setAttribute("paymentMessage", "User not found");
            session.setAttribute("userId", userId);
            session.setAttribute("premiumUser", false);
            response.sendRedirect(PAYMENT_STATUS_URL);
            PaymentResponse paymentResponse = new PaymentResponse(PAYMENT_STATUS_URL, "Error - no user with given userId", false, userId);
            return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setPremiumUser(false);
        userRepository.save(user);

        session.setAttribute("paymentStatus", "cancelled");
        session.setAttribute("paymentMessage", "Payment canceled");
        session.setAttribute("userId", userId);
        session.setAttribute("premiumUser", false);
        response.sendRedirect(PAYMENT_STATUS_URL);

        PaymentResponse paymentResponse = new PaymentResponse(PAYMENT_STATUS_URL, "Payment canceled", true, userId);
        return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
    }

    @GetMapping("/pay/success")
    public ResponseEntity<PaymentResponse> successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId, @RequestParam("userId") String userId, HttpSession session, HttpServletResponse response) throws IOException {
        if (userId == null) {
            session.setAttribute("paymentStatus", "cancelled");
            session.setAttribute("paymentMessage", "User not found");
            session.setAttribute("userId", userId);
            session.setAttribute("premiumUser", false);
            response.sendRedirect(PAYMENT_STATUS_URL);
            PaymentResponse paymentResponse = new PaymentResponse(PAYMENT_STATUS_URL, "Error - no user with given userId", false, userId);
            return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
        }
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
                user.setPremiumUser(true);
                userRepository.save(user);
                session.setAttribute("paymentStatus", "not_approved");
                session.setAttribute("paymentMessage", "Payment successful");
                session.setAttribute("userId", userId);
                session.setAttribute("premiumUser", true);
                response.sendRedirect(PAYMENT_STATUS_URL);
                PaymentResponse paymentResponse = new PaymentResponse(PAYMENT_STATUS_URL, "Payment successful", true, userId);
                return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
            }
        } catch (PayPalRESTException e) {
            throw new RuntimeException(e);
        }
        session.setAttribute("paymentStatus", "not_approved");
        session.setAttribute("paymentMessage", "Payment not approved");
        session.setAttribute("userId", userId);
        session.setAttribute("premiumUser", false);
        response.sendRedirect(PAYMENT_STATUS_URL);
        PaymentResponse paymentResponse = new PaymentResponse(PAYMENT_STATUS_URL, "Payment error", false, userId);
        return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
    }

    @GetMapping("/sessionData")
    public ResponseEntity<Map<String, Object>> getSessionData(HttpSession session) {
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("paymentStatus", session.getAttribute("paymentStatus"));
        sessionData.put("paymentMessage", session.getAttribute("paymentMessage"));
        sessionData.put("userId", session.getAttribute("userId"));
        sessionData.put("premiumUser", session.getAttribute("premiumUser"));
        return ResponseEntity.ok(sessionData);
    }

}
