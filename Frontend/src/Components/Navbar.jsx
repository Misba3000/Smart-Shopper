import React from "react";
import {
  AppBar,
  Toolbar,
  Typography,
  IconButton,
  InputBase,
  Box,
} from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import FavoriteBorderIcon from "@mui/icons-material/FavoriteBorder";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import { useNavigate } from "react-router-dom";

const Navbar = () => {
  const navigate = useNavigate();
  const isLoggedIn = false; // TODO: replace with Redux state or auth check

  const handleWishlistClick = () => {
    if (!isLoggedIn) {
      navigate("/register");
    } else {
      navigate("/wishlist");
    }
  };

  const handleUserClick = () => {
    if (!isLoggedIn) {
      navigate("/", { state: { openLogin: true } });
    } else {
      navigate("/profile");
    }
  };

  return (
    <AppBar position="sticky" sx={{ bgcolor: "#1976d2" }}>
      <Toolbar sx={{ display: "flex", justifyContent: "space-between" }}>
        {/* Logo */}
        <Typography
          variant="h6"
          sx={{
            fontWeight: "bold",
            cursor: "pointer",
          }}
          onClick={() => navigate("/")}
        >
          SmartShopper
        </Typography>

        {/* Home Link */}
        <Typography
          variant="body1"
          sx={{
            ml: 4,
            cursor: "pointer",
            display: { xs: "none", md: "block" },
          }}
          onClick={() => navigate("/")}
        >
          Home
        </Typography>

        {/* Search Bar */}
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            bgcolor: "white",
            borderRadius: 1,
            px: 1,
            mx: 2,
            width: { xs: "40%", md: "50%" },
          }}
        >
          <SearchIcon color="action" />
          <InputBase placeholder="Search products..." sx={{ ml: 1, flex: 1 }} />
        </Box>

        {/* Icons */}
        <Box sx={{ display: "flex", alignItems: "center" }}>
          <IconButton color="inherit" onClick={handleWishlistClick}>
            <FavoriteBorderIcon />
          </IconButton>
          <IconButton color="inherit" onClick={handleUserClick}>
            <AccountCircleIcon />
          </IconButton>
        </Box>
      </Toolbar>
    </AppBar>
  );
};

export default Navbar;
