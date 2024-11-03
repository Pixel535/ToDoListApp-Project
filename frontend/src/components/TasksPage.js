import React, { useEffect, useState } from "react";
import Task from "./Task";
import Button from "@mui/material/Button";
import { useNavigate } from "react-router-dom";

function TasksPage({ jwt, userId, username, premiumUser }) {
    const [tasks, setTasks] = useState([]);
    const [isDarkMode, setIsDarkMode] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        if (!jwt) {
            navigate("/");
            return;
        }

        fetch("http://localhost:8080/api/tasks", {
            headers: {
                "Authorization": `Bearer ${jwt}`,
                "Content-Type": "application/json",
                "userId": userId
            }
        })
        .then((response) => response.json())
        .then((data) => setTasks(data));
    }, [jwt, userId, navigate]);

    function addNewTask() {
        const newTask = { title: "New Task", description: "", completed: false };
        fetch("http://localhost:8080/api/tasks", {
            headers: {
                "Authorization": `Bearer ${jwt}`,
                "Content-Type": "application/json",
                "userId": userId
            },
            method: "POST",
            body: JSON.stringify(newTask)
        })
        .then((response) => response.json())
        .then((data) => setTasks([...tasks, data]));
    }

    function handleDeleteTask(item) {
        const updatedTasks = tasks.filter((data) => data.id !== item.id);
        setTasks(updatedTasks);
    }

    function handleLogout() {
        localStorage.removeItem("jwt");
        localStorage.removeItem("username");
        localStorage.removeItem("userId");
        localStorage.removeItem("premiumUser");
        navigate("/");
    }

    function handlePayForPremium() {
        navigate("/paymentInfo");
    }

    function toggleDarkMode() {
            setIsDarkMode(!isDarkMode);
    }

    return (
        <div className={`main-body ${isDarkMode ? "dark-mode" : ""}`}>
            <div className="todo-container">
                <h2 className="text-center">To Do List</h2>
                <p className="text-center">Hello, {username}! Are you premium ? [{premiumUser ? "Yes" : "No"}]</p>
                <div className="addbtn">
                    <Button variant="contained" onClick={addNewTask}>
                        Add task
                    </Button>
                    {premiumUser ? (
                        <Button variant="contained" color="secondary" onClick={toggleDarkMode}>
                            {isDarkMode ? "Switch to Light Mode" : "Switch to Dark Mode"}
                        </Button>
                    ) : (
                        <Button variant="contained" color="primary" onClick={handlePayForPremium}>
                            Pay for Dark Mode
                        </Button>
                    )}
                    <Button variant="contained" color="secondary" onClick={handleLogout}>
                        Log Out
                    </Button>
                </div>
                <div className="tasks">
                    {tasks.length > 0
                        ? tasks.map((task) => (
                            <Task data={task} key={task.id} jwt={jwt} userId={userId} emitDeleteTask={handleDeleteTask} />
                        ))
                        : "There are no Tasks left to do!"}
                </div>
            </div>
        </div>
    );
}

export default TasksPage;
