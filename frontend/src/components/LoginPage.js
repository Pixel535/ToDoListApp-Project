import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { MDBContainer, MDBInput } from 'mdb-react-ui-kit';
import Button from "@mui/material/Button";

function LoginPage({ setJwt, setUsername, setUserId }) {
    const [username, setLocalUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleLogin = async () => {
        try {
            if (!username || !password) {
                setError('Please enter both username and password.');
                return;
            }

            const response = await axios.post('http://localhost:8080/auth/signin', { username, password });
            console.log('Login successful:', response.data);

            setJwt(response.data.jwt);
            setUsername(username);
            setUserId(response.data.userId);

            navigate('/tasks');
        } catch (error) {
            console.error('Login failed:', error.response ? error.response.data : error.message);
            setError('Invalid username or password.');
        }
    };

    return (
        <div className="main-body">
            <div className="login-register-container">
                <MDBContainer className="p-3">
                    <h2 className="mb-4 text-center">Login</h2>
                    <MDBInput wrapperClass='mb-3' placeholder='Username' id='username' value={username}
                              onChange={(e) => setLocalUsername(e.target.value)} />
                    <MDBInput wrapperClass='mb-3' placeholder='Password' id='password' type='password' value={password}
                              onChange={(e) => setPassword(e.target.value)} />
                    {error && <p className="text-danger">{error}</p>}
                    <Button variant="contained" onClick={handleLogin}>
                        Sign in
                    </Button>
                    <div className="text-center">
                        <p>Not a member? <a href="/signup">Register</a></p>
                    </div>
                </MDBContainer>
            </div>
        </div>
    );
}

export default LoginPage;
