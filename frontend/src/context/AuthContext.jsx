import React, { createContext, useContext, useState, useEffect } from 'react';
import axios from 'axios';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    try {
      const savedUser = localStorage.getItem('smartShopperUser');
      if (savedUser && savedUser !== "undefined") {
        setUser(JSON.parse(savedUser));
      }
    } catch (err) {
      console.error("Error parsing saved user:", err);
      localStorage.removeItem('smartShopperUser');
    }
    setLoading(false);
  }, []);

  // ✅ Register API call
  const register = async (name, email, password) => {
    try {
      const response = await axios.post("http://localhost:8080/api/auth/register", {
        name,
        email,
        password,
        role: "USER"
      });

      console.log("Registration response:", response.data);
      return response.data;
    } catch (error) {
      console.error("Registration failed", error);
      const errorMessage = error.response?.data?.message || "Registration failed. Please try again.";
      throw new Error(errorMessage);
    }
  };

  // ✅ Login API call - Support both admin and regular users
  const login = async (email, password, verificationCode = '') => {
    try {
      const response = await axios.post("http://localhost:8080/api/auth/login", {
        email,
        password,
        verificationCode: verificationCode || undefined, // Send undefined if empty
        role: "USER"
      });

      console.log("Login response:", response.data);

      const loggedInUser = response.data.data;
      setUser(loggedInUser);
      localStorage.setItem("smartShopperUser", JSON.stringify(loggedInUser));
      
      // Log role for debugging
      console.log("User role:", loggedInUser.role);
      
      return loggedInUser;
    } catch (error) {
      console.error("Login failed", error);
      const errorMessage = error.response?.data?.message || "Login failed. Please check your credentials.";
      throw new Error(errorMessage);
    }
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('smartShopperUser');
  };

  // Helper function to check if user is admin
  const isAdmin = () => {
    return user?.role === 'ADMIN';
  };

  const value = {
    user,
    login,
    register,
    logout,
    loading,
    isAdmin,
  };

  return (
    <AuthContext.Provider value={value}>
      {!loading && children}
    </AuthContext.Provider>
  );
};