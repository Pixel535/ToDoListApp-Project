import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { MDBContainer, MDBInput } from 'mdb-react-ui-kit';
import Button from "@mui/material/Button";

function SignupPage({ setJwt, setUsername, setUserId, setPremiumUser }) {
    const [username, setLocalUsername] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSignup = async () => {
        try {
            if (!username || !password || !confirmPassword) {
                setError('Please fill in all fields.');
                return;
            }

            if (password !== confirmPassword) {
                setError('Passwords do not match');
                return;
            }

            const response = await axios.post('http://localhost:8080/auth/signup', {
                username,
                password
            });

            console.log(response.data);
            setJwt(response.data.jwt);
            setUsername(username);
            setUserId(response.data.userId);
            setPremiumUser(response.data.premiumUser);

            navigate('/tasks');
        } catch (error) {
            console.error('Signup failed:', error.response ? error.response.data : error.message);
            setError(error.response ? error.response.data : error.message);
        }
    };

    return (
        <div className="main-body">
            <div className="login-register-container">
                <MDBContainer className="p-3">
                    <h2 className="text-center">Sign Up</h2>
                    {error && <p className="text-danger">{error}</p>}
                    <MDBInput wrapperClass='mb-3' placeholder='Username' id='username' value={username}
                              onChange={(e) => setLocalUsername(e.target.value)} />
                    <MDBInput wrapperClass='mb-3' placeholder='Password' id='password' type='password' value={password}
                              onChange={(e) => setPassword(e.target.value)} />
                    <MDBInput wrapperClass='mb-3' placeholder='Confirm Password' id='confirmPassword' type='password'
                              value={confirmPassword}
                              onChange={(e) => setConfirmPassword(e.target.value)} />
                    <Button variant="contained"
                            onClick={handleSignup}>Sign Up
                    </Button>
                    <div className="text-center">
                        <p>Already registered? <a href="/">Login</a></p>
                    </div>
                </MDBContainer>
            </div>
        </div>
    );
}

export default SignupPage;
