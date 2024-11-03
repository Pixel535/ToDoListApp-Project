import "./App.css";
import React, { useState, useEffect } from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import LoginPage from "./components/LoginPage";
import SignupPage from "./components/SignUpPage";
import TasksPage from "./components/TasksPage";
import PaymentInfo from "./components/PaymentInfo";
import PaymentStatus from './components/PaymentStatus';
import PrivateRoute from './components/PrivateRoute';

function App() {
    const [jwt, setJwt] = useState(localStorage.getItem("jwt") || "");
    const [username, setUsername] = useState(localStorage.getItem("username") || "");
    const [userId, setUserId] = useState(localStorage.getItem("userId") || "");
    const [premiumUser, setPremiumUser] = useState(localStorage.getItem("premiumUser") === "true");

    useEffect(() => {
        if (jwt) localStorage.setItem("jwt", jwt);
        else localStorage.removeItem("jwt");

        localStorage.setItem("username", username);
        localStorage.setItem("userId", userId);
        localStorage.setItem("premiumUser", premiumUser);
    }, [jwt, username, userId, premiumUser]);

    return (
        <Router>
            <Routes>
                <Route path="/" element={<LoginPage setJwt={setJwt} setUsername={setUsername} setUserId={setUserId} setPremiumUser={setPremiumUser} />} />
                <Route path="/signup" element={<SignupPage setJwt={setJwt} setUsername={setUsername} setUserId={setUserId} setPremiumUser={setPremiumUser} />} />
                <Route path="/tasks" element={
                    <PrivateRoute jwt={jwt}>
                        <TasksPage jwt={jwt} username={username} userId={userId} premiumUser={premiumUser} />
                    </PrivateRoute>
                } />
                <Route path="/paymentInfo" element={
                    <PrivateRoute jwt={jwt}>
                        <PaymentInfo jwt={jwt} username={username} userId={userId} premiumUser={premiumUser} />
                    </PrivateRoute>
                } />
                <Route path="/paymentStatus" element={<PaymentStatus setJwt={setJwt} setUsername={setUsername} setUserId={setUserId} setPremiumUser={setPremiumUser} />} />
            </Routes>
        </Router>
    );
}

export default App;
