import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

function PaymentStatus({ setJwt, setUsername, setUserId, setPremiumUser }) {
    const [message, setMessage] = useState("Loading payment status...");
    const [success, setSuccess] = useState(false);
    const jwt = localStorage.getItem("jwt");
    const userId = localStorage.getItem("userId");
    const username = localStorage.getItem("username");
    const premiumUser = localStorage.getItem("premiumUser");
    const navigate = useNavigate();

    setJwt(jwt);
    setUsername(username);
    setUserId(userId);

    useEffect(() => {
        if (!jwt) {
           navigate("/");
           return;
        }
        fetch("http://localhost:8080/api/payment/sessionData", {
            method: "GET",
            credentials: "include",
        })
        .then(response => response.json())
        .then(data => {
            setMessage(data.paymentMessage || "Unknown status");
            setSuccess(data.paymentStatus === "approved");
            setPremiumUser(data.premiumUser);
        })
        .catch(error => {
            console.error("Error fetching session data:", error);
            setMessage("Error fetching payment status.");
        });

        const timer = setTimeout(() => {
            navigate('/tasks');
        }, 5000);

        return () => clearTimeout(timer);
    }, [navigate]);

    return (
        <div style={{ textAlign: 'center', marginTop: '20%' }}>
            <h1>{message}</h1>
            <p>You will be redirected to tasks in 5 seconds...</p>
        </div>
    );
}

export default PaymentStatus;
