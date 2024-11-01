import "./App.css";
import React, { useState } from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import LoginPage from "./components/LoginPage";
import SignupPage from "./components/SignUpPage";
import TasksPage from "./components/TasksPage";

function App() {
    const [jwt, setJwt] = useState("");
    const [username, setUsername] = useState("");
    const [userId, setUserId] = useState("");

    return (
        <Router>
            <Routes>
                <Route path="/" element={<LoginPage setJwt={setJwt} setUsername={setUsername} setUserId={setUserId} />} />
                <Route path="/signup" element={<SignupPage setJwt={setJwt} setUsername={setUsername} setUserId={setUserId} />} />
                <Route path="/tasks" element={<TasksPage jwt={jwt} username={username} userId={userId} />} />
            </Routes>
        </Router>
    );
}

export default App;
