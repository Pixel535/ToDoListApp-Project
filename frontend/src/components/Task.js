import { Checkbox, IconButton, TextField } from "@mui/material";
import { LocalizationProvider } from "@mui/x-date-pickers";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import React, { useEffect, useState } from "react";
import DeleteIcon from "@mui/icons-material/Delete";
import dayjs from "dayjs";

const Task = (props) => {
    const { emitDeleteTask, jwt, userId } = props;
    const [task, setTask] = useState(props.data);
    const [isDirty, setDirty] = useState(false);

    useEffect(() => {
        if (isDirty) {
            fetch(`http://localhost:8080/api/tasks/${task.id}`, {
                method: "PUT",
                headers: {
                    "Authorization": `Bearer ${jwt}`,
                    "Content-Type": "application/json",
                    "userId": userId
                },
                body: JSON.stringify(task)
            })
            .then((response) => response.json())
            .then((data) => {
                setDirty(false);
                setTask(data);
            });
        }
    }, [task, isDirty, jwt, userId]);

    function updateTaskTitle(e) {
        setDirty(true);
        setTask({ ...task, title: e.target.value });
    }

    function deleteTask() {
        fetch(`http://localhost:8080/api/tasks/${task.id}`, {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${jwt}`,
                "Content-Type": "application/json",
                "userId": userId
            }
        }).then(() => {
            emitDeleteTask(task);
        });
    }

    function updateDueDate(newDate) {
        setDirty(true);
        setTask({ ...task, dueDate: newDate ? newDate.format("YYYY-MM-DD") : null });
    }

    return (
        <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
            <Checkbox
                checked={task.completed}
                onChange={() => {
                    setDirty(true);
                    setTask({ ...task, completed: !task.completed });
                }}
            />
            <input
                type="text"
                className={task.completed ? "done" : ""}
                value={task.title}
                onChange={updateTaskTitle}
            />
            <LocalizationProvider dateAdapter={AdapterDayjs}>
                <DatePicker
                    label="Due Date"
                    format="DD/MM/YYYY"
                    value={task.dueDate ? dayjs(task.dueDate) : null}
                    onChange={(newValue) => updateDueDate(newValue)}
                    renderInput={(params) => <TextField {...params} />}
                />
            </LocalizationProvider>
            <IconButton aria-label="delete" size="large" onClick={deleteTask}>
                <DeleteIcon fontSize="inherit" color="primary" />
            </IconButton>
        </div>
    );
};

export default Task;
