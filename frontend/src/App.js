import "./App.css";
import React, { useEffect, useState } from "react";
import Task from "./components/Task";
import Button from "@mui/material/Button";

function App() {
	const [tasks, setTasks] = useState([]);

	useEffect(() => {
		if (!tasks.length) {
			fetch("http://localhost:8080/api/tasks")
				.then((response) => response.json())
				.then((data) => setTasks(data));
		}
	}, [tasks]);

	function addNewTask() {
		const newTask = { title: "New Task", description: "", completed: false };
		fetch("http://localhost:8080/api/tasks", {
			headers: {
				"content-type": "application/json"
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

	return (
		<div className="main-body">
			<div className="todo-container">
				<div className="above-label">
					<h2 style={{ textTransform: "uppercase" }}>To Do List</h2>
				</div>
				<div className="addbtn">
					<Button variant="contained" onClick={addNewTask}>
						Add task
					</Button>
				</div>
				<div className="tasks">
					{tasks.length > 0
						? tasks.map((task) => (
								<Task
									data={task}
									key={task.id}
									emitDeleteTask={handleDeleteTask}
								/>
						  ))
						: "There are no Tasks left to do !"}
				</div>
			</div>
		</div>
	);
}

export default App;
