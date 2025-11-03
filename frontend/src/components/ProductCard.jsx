import React, { useState } from "react";
import {
  Card,
  CardMedia,
  CardContent,
  Typography,
  Box,
  Button,
  Chip,
  Rating,
  Tooltip,
} from "@mui/material";
import { ShoppingCartOutlined, OpenInNew } from "@mui/icons-material";
import axios from "axios";
import { toast } from "react-toastify";

const ProductCard = ({ product }) => {
  const [imageError, setImageError] = useState(false);
  const [isAdding, setIsAdding] = useState(false);

  const formatPrice = (price) =>
    price ? `₹${price.toLocaleString("en-IN")}` : "₹0";

  const getPlatformColor = (platform) => {
    switch (platform?.toLowerCase()) {
      case "amazon":
        return "#ff9900";
      case "meesho":
        return "#f43397";
      case "flipkart":
        return "#2874f0";
      default:
        return "#6366f1";
    }
  };

  // ✅ Add to Wishlist instantly
  const addToWishlist = async () => {
    try {
      const userData = JSON.parse(localStorage.getItem("smartShopperUser"));
      if (!userData?.id) {
        toast.error("Please login to add items to your wishlist!");
        return;
      }

      setIsAdding(true);

      const response = await axios.post(
        `http://localhost:8080/api/wishlist/${userData.id}`,
        {
          title: product.title || "Unknown Product",
          brand: product.brand || "Unknown Brand",
          platform: product.platform || "Unknown Platform",
          productUrl: product.productUrl?.trim() || `no-url-${Date.now()}`,
          price: product.price ?? 0,
          imageUrl: product.imageUrl || "/default-product.png",
          source: product.source || "Unknown",
          rating: product.rating ?? 0,
          reviewCount: product.reviewCount ?? 0,
          description: product.description || "No description available.",
          targetPrice: null,
          alertEnabled: false,
        }
      );

      console.log("✅ Added to wishlist:", response.data);
      toast.success("Product added to your wishlist!");
    } catch (err) {
      console.error("❌ Error adding to wishlist:", err);
      if (err.response?.status === 409)
        toast.info("This product is already in your wishlist.");
      else toast.error("Failed to add product to wishlist!");
    } finally {
      setIsAdding(false);
    }
  };

  return (
    <Card
      sx={{
        display: "flex",
        flexDirection: "column",
        borderRadius: 2,
        boxShadow: "0 2px 8px rgba(0,0,0,0.1)",
        maxWidth: 300,
        gap: 2,
        marginX: 4,
        marginY: 2,
        transition: "0.2s",
        "&:hover": {
          transform: "translateY(-4px)",
          boxShadow: "0 8px 24px rgba(0,0,0,0.15)",
        },
      }}
    >
      <Box sx={{ position: "relative" }}>
        <CardMedia
          component="img"
          height="200"
          image={
            imageError ? "/placeholder.png" : product.imageUrl || "/placeholder.png"
          }
          alt={product.title}
          sx={{ objectFit: "contain", p: 1, backgroundColor: "#f5f5f5" }}
          onError={() => setImageError(true)}
        />
        <Chip
          label={product.platform || "Unknown"}
          size="small"
          sx={{
            position: "absolute",
            top: 8,
            left: 8,
            backgroundColor: getPlatformColor(product.platform),
            color: "white",
            fontWeight: "bold",
          }}
        />
      </Box>

      <CardContent
        sx={{ flexGrow: 1, display: "flex", flexDirection: "column" }}
      >
        <Tooltip title={product.title} arrow>
          <Typography
            variant="h6"
            sx={{
              fontWeight: "bold",
              mb: 1,
              overflow: "hidden",
              textOverflow: "ellipsis",
              display: "-webkit-box",
              WebkitLineClamp: 2,
              WebkitBoxOrient: "vertical",
              minHeight: "64px",
            }}
          >
            {product.title}
          </Typography>
        </Tooltip>

        {product.brand && product.brand !== "Unknown" && (
          <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
            Brand: {product.brand}
          </Typography>
        )}

        {product.rating && (
          <Box sx={{ display: "flex", alignItems: "center", mb: 1 }}>
            <Rating
              value={product.rating}
              precision={0.1}
              size="small"
              readOnly
            />
            {product.reviewCount && (
              <Typography variant="body2" sx={{ ml: 1 }}>
                ({product.reviewCount})
              </Typography>
            )}
          </Box>
        )}

        <Typography
          variant="h5"
          sx={{ fontWeight: "bold", color: "#6366f1", mt: "auto" }}
        >
          {formatPrice(product.price)}
        </Typography>

        <Box sx={{ display: "flex", gap: 1, mt: 2 }}>
          <Button
            variant="contained"
            fullWidth
            startIcon={<ShoppingCartOutlined />}
            onClick={addToWishlist}
            disabled={isAdding}
            sx={{
              backgroundColor: "#6366f1",
              "&:hover": { backgroundColor: "#4f46e5" },
            }}
          >
            {isAdding ? "Adding..." : "Add to Wishlist"}
          </Button>
          <Button
            variant="outlined"
            onClick={() => window.open(product.productUrl, "_blank")}
          >
            <OpenInNew />
          </Button>
        </Box>
      </CardContent>
    </Card>
  );
};

export default ProductCard;
