import React from "react";
import {
  Card,
  CardContent,
  CardMedia,
  Typography,
  CardActions,
  Button,
  Chip,
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";

const ProductCard = ({ product }) => {
  const navigate = useNavigate();
  const isLoggedIn = useSelector((state) => state.auth.isLoggedIn);

  const handleViewDetails = () => {
    if (!isLoggedIn) {
      navigate("/landing");
    } else {
      navigate(`/product/${product.name}`);
    }
  };

  const handleAddToWishlist = () => {
    if (!isLoggedIn) {
      navigate("/landing");
    } else {
      navigate("/wishlist"); // Navigate to wishlist route
    }
  };

  return (
    <Card
      sx={{
        maxWidth: 300,
        m: 2,
        borderRadius: 3,
        boxShadow: 3,
        transition: "transform 0.3s ease, box-shadow 0.3s ease",
        "&:hover": {
          transform: "translateY(-5px)",
          boxShadow: 6,
        },
      }}
    >
      <CardMedia
        component="img"
        height="200"
        image={product.image}
        alt={product.name}
        sx={{ objectFit: "contain", p: 2, cursor: "pointer" }}
        onClick={handleViewDetails}
      />
      <CardContent>
        <Typography gutterBottom variant="h6" noWrap>
          {product.name}
        </Typography>
        <Typography variant="body2" color="text.secondary" noWrap>
          {product.brand}
        </Typography>
        <Chip
          label={`₹${product.price}`}
          color="primary"
          sx={{ mt: 1, fontWeight: "bold" }}
        />
      </CardContent>
      <CardActions>
        <Button
          size="small"
          variant="contained"
          sx={{ bgcolor: "#1976d2", "&:hover": { bgcolor: "#145ca4" } }}
          onClick={handleViewDetails}
        >
          View Details
        </Button>
        <Button size="small" variant="outlined" color="primary" onClick={handleAddToWishlist}>
          Wishlist
        </Button>
      </CardActions>
    </Card>
  );
};

export default ProductCard;
