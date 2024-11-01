import React, { useEffect, useState } from "react";
import Task from "./Task";
import Button from "@mui/material/Button";
import { useNavigate } from "react-router-dom";

function TasksPage({ jwt, userId, username }) {
    const [tasks, setTasks] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        if (jwt) {
            fetch("http://localhost:8080/api/tasks", {
                headers: {
                    "Authorization": `Bearer ${jwt}`,
                    "Content-Type": "application/json",
                    "userId": userId
                }
            })
            .then((response) => response.json())
            .then((data) => setTasks(data));
        }
    }, [jwt, userId]);

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
        navigate("/");
    }

    return (
        <div className="main-body">
            <div className="todo-container">
                <h2 className="text-center">To Do List</h2>
                <p className="text-center">Hello, {username}!</p>
                <div className="addbtn">
                    <Button variant="contained" onClick={addNewTask}>
                        Add task
                    </Button>
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
