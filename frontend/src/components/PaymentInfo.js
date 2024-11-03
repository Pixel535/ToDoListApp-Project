import React from "react";
import Button from "@mui/material/Button";
import { useNavigate } from "react-router-dom";

function PaymentInfo({ jwt, userId, username, premiumUser }) {
    const navigate = useNavigate();

    if (!jwt) {
        navigate("/");
        return null;
    }

    function handleGoBack() {
        navigate("/tasks");
    }

    function handlePay() {
        const paymentData = {
            amount: 5.0,
            currency: "USD",
            method: "paypal",
            intent: "Sale",
            description: "Payment for Dark Mode"
        };

        localStorage.setItem("jwt", jwt);
        localStorage.setItem("userId", userId);
        localStorage.setItem("username", username);
        localStorage.setItem("premiumUser", premiumUser);

        fetch("http://localhost:8080/api/payment/pay", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${jwt}`,
                "userId": userId
            },
            body: JSON.stringify(paymentData)
        })
        .then(response => response.json())
        .then(data => {
            if (data.success && data.redirectUrl) {
                window.location.href = data.redirectUrl;
            } else {
                console.error("Payment error:", data.message);
            }
        })
        .catch(error => {
            console.error("Payment error", error);
        });
    }

    return (
        <div style={{ textAlign: "center", marginTop: "20px" }}>
            <h2>Pay for Dark Mode</h2>
            <p>Total: 5.00</p>
            <p>Currency: USD</p>
            <p>Method: Paypal</p>
            <p>Intent: Sale</p>
            <p>Description: Payment for Dark Mode</p>
            <Button variant="contained" onClick={handleGoBack}>
                Go Back
            </Button>
            <Button variant="contained" color="primary" onClick={handlePay} style={{ marginLeft: "10px" }}>
                Pay
            </Button>
        </div>
    );
}

export default PaymentInfo;
