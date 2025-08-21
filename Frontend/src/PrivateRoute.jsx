// src/PrivateRoute.jsx
import React from "react";
import { Navigate, useLocation } from "react-router-dom";
import { useSelector } from "react-redux";

const PrivateRoute = ({ children }) => {
  const isLoggedIn = useSelector((state) => state.auth.isLoggedIn);
  const location = useLocation();

  if (!isLoggedIn) {
    // Redirect to landing page if not logged in
    return <Navigate to="/landing" state={{ from: location }} replace />;
  }

  return children;
};

export default PrivateRoute;
