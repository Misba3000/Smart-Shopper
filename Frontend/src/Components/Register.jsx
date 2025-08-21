import React, { useState } from "react";
import { registerUser } from "../loginData";
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

const Register = () => {
  const [formData, setFormData] = useState({ email: "", password: "" });
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  const handleChange = (e) =>
    setFormData({ ...formData, [e.target.name]: e.target.value });

  const handleSubmit = (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage("");

    setTimeout(() => {
      const success = registerUser(formData);
      if (success) {
        setMessage("Registration successful!");
      } else {
        setMessage("Email already registered!");
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
        Register a New Account
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

      {message && (
        <Typography color="primary" variant="body2" mt={1}>
          {message}
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
        {loading ? <CircularProgress size={24} /> : "Register"}
      </Button>
    </Box>
  );
};

export default Register;
