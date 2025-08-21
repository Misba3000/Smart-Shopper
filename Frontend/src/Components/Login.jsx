import React, { useState } from "react";
import { useDispatch } from "react-redux";
import { useNavigate, useLocation } from "react-router-dom";
import { login } from "../Feature/auth/authSlice"; // Redux slice action
import {
  Button,
  TextField,
  Box,
  Typography,
  InputAdornment,
  IconButton,
  CircularProgress,
} from "@mui/material";
import { Visibility, VisibilityOff } from "@mui/icons-material";

const Login = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const location = useLocation();
  const from = location.state?.from?.pathname || "/"; // redirect after login

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });
  const [showPassword, setShowPassword] = useState(false);

  const handleChange = (e) =>
    setFormData({ ...formData, [e.target.name]: e.target.value });

  const handleSubmit = (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    // Simulate login delay
    setTimeout(() => {
      // Here you can validate with your loginDetails array if you want
      const validUser = true; // For example purposes, assume any login is valid
      if (validUser) {
        dispatch(login({ email: formData.email })); // update Redux state
        navigate(from, { replace: true }); // redirect to previous page or home
      } else {
        setError("Invalid email or password");
      }
      setLoading(false);
    }, 500);
  };

  return (
    <Box
      component="form"
      onSubmit={handleSubmit}
      sx={{
        width: "100%",
        maxWidth: 400,
        margin: "0 auto",
        padding: 4,
        border: "1px solid #ccc",
        borderRadius: 2,
        backgroundColor: "#fff",
      }}
    >
      <Typography variant="h5" mb={2} align="center">
        Login to Your Account
      </Typography>

      <TextField
        label="Email"
        name="email"
        type="email"
        fullWidth
        margin="normal"
        required
        value={formData.email}
        onChange={handleChange}
      />

      <TextField
        label="Password"
        name="password"
        type={showPassword ? "text" : "password"}
        fullWidth
        margin="normal"
        required
        value={formData.password}
        onChange={handleChange}
        InputProps={{
          endAdornment: (
            <InputAdornment position="end">
              <IconButton
                onClick={() => setShowPassword(!showPassword)}
                edge="end"
              >
                {showPassword ? <VisibilityOff /> : <Visibility />}
              </IconButton>
            </InputAdornment>
          ),
        }}
      />

      {error && (
        <Typography color="error" variant="body2" mt={1}>
          {error}
        </Typography>
      )}

      <Button
        type="submit"
        variant="contained"
        fullWidth
        color="primary"
        disabled={loading}
        sx={{ mt: 2 }}
      >
        {loading ? <CircularProgress size={24} /> : "Login"}
      </Button>
    </Box>
  );
};

export default Login;
