import React, { useState } from "react";
import { Grid, Box, Typography, Button } from "@mui/material";
import Login from "../../Components/Login";
import Register from "../../Components/Register";

const LandingPage = () => {
  const [showLogin, setShowLogin] = useState(true);

  return (
    <Grid container sx={{ height: "100vh" }}>
      {/* Left Side */}
      <Grid
        item
        xs={12}
        md={6}
        sx={{
          backgroundColor: "#f4f6f8",
          display: "flex",
          flexDirection: "column",
          justifyContent: "center",
          padding: 6,
        }}
      >
        <Typography variant="h3" fontWeight="bold" gutterBottom>
          Welcome to SmartShopper
        </Typography>
        <Typography variant="h6" color="text.secondary" gutterBottom>
          Compare prices, track deals, and never overpay again!
        </Typography>
        <Typography variant="body1" mt={2}>
          Smart Product Price Comparison helps you track and compare prices across Amazon, Flipkart, Myntra, and more.
          Save money, set alerts, and visualize price history before buying. Your smart shopping assistant is here.
        </Typography>
      </Grid>

      {/* Right Side */}
      <Grid
        item
        xs={12}
        md={6}
        sx={{
          display: "flex",
          flexDirection: "column",
          justifyContent: "center",
          alignItems: "center",
          padding: 4,
        }}
      >
        <Box sx={{ width: "100%", maxWidth: 450 }}>
          <Box sx={{ display: "flex", justifyContent: "center", mb: 2 }}>
            <Button
              onClick={() => setShowLogin(true)}
              variant={showLogin ? "contained" : "outlined"}
              sx={{ mr: 1 }}
            >
              Login
            </Button>
            <Button
              onClick={() => setShowLogin(false)}
              variant={!showLogin ? "contained" : "outlined"}
            >
              Register
            </Button>
          </Box>

          {showLogin ? <Login /> : <Register />}
        </Box>
      </Grid>
    </Grid>
  );
};

export default LandingPage;
