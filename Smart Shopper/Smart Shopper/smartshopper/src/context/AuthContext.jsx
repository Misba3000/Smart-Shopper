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
      localStorage.removeItem('smartShopperUser'); // cleanup bad data
    }
    setLoading(false);
  }, []);

  // ✅ Login API call
  const login = async (email, password) => {
    try {
      const response = await axios.post("http://localhost:8080/api/auth/login", {
        email,
        password
      });

      const loggedInUser = response.data; // since your backend wraps in ApiResponse
      setUser(loggedInUser);
      localStorage.setItem("smartShopperUser", JSON.stringify(loggedInUser || {}));
      return loggedInUser;
    } catch (error) {
      console.error("Login failed", error);
      throw error;
    }
  };

  // ✅ Register API call
  const register = async (name, email, password) => {
    try {
      const response = await axios.post("http://localhost:8080/api/users", {
        name,
        email,
        password
      });

      const newUser = response.data.data;
      setUser(newUser);
      localStorage.setItem("smartShopperUser", JSON.stringify(newUser));
      return newUser;
    } catch (error) {
      console.error("Registration failed", error);
      throw error;
    }
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('smartShopperUser');
  };

  const value = {
    user,
    login,
    register,
    logout,
    loading,
  };

  return (
    <AuthContext.Provider value={value}>
      {!loading && children}
    </AuthContext.Provider>
  );
};
