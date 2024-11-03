import React from 'react';
import { Navigate } from 'react-router-dom';

function PrivateRoute({ children, jwt }) {
    return jwt ? children : <Navigate to="/" replace />;
}

export default PrivateRoute;
