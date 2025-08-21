import React from "react";
import { Box, Typography, Link, Grid } from "@mui/material";

const Footer = () => {
  return (
    <Box
      component="footer"
      sx={{
        bgcolor: "#1976d2",
        color: "white",
        py: 4,
        mt: 5,
      }}
    >
      <Grid container spacing={2} justifyContent="center">
        <Grid item xs={12} md={4} textAlign="center">
          <Typography variant="h6" fontWeight="bold">
            SmartShopper
          </Typography>
          <Typography variant="body2" mt={1}>
            Compare prices, track deals, and shop smarter!
          </Typography>
        </Grid>
        <Grid item xs={12} md={4} textAlign="center">
          <Typography variant="subtitle1" mb={1}>
            Quick Links
          </Typography>
          {["Home", "Dashboard", "Compare"].map((link) => (
            <Link
              key={link}
              href="#"
              color="inherit"
              underline="hover"
              display="block"
              sx={{ "&:hover": { color: "#ffeb3b" } }}
            >
              {link}
            </Link>
          ))}
        </Grid>
        <Grid item xs={12} md={4} textAlign="center">
          <Typography variant="subtitle1" mb={1}>
            Contact Us
          </Typography>
          <Typography variant="body2">support@smartshopper.com</Typography>
        </Grid>
      </Grid>

      <Typography variant="body2" align="center" mt={3}>
        © {new Date().getFullYear()} SmartShopper. All rights reserved.
      </Typography>
    </Box>
  );
};

export default Footer;
