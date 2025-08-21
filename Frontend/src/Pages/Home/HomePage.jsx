import React, { useEffect, useState } from "react";
import { Container, Grid, Typography } from "@mui/material";
import Loader from "../../Components/Loader";
import ProductCard from "../../Components/ProductCard";

const HomePage = () => {
  const [productCards, setProductCards] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setTimeout(() => {
      setProductCards([
        {
          name: "Apple iPhone 14",
          brand: "Apple",
          price: 69999,
          image: "https://via.placeholder.com/300",
        },
        {
          name: "Samsung Galaxy S23",
          brand: "Samsung",
          price: 64999,
          image: "https://via.placeholder.com/300",
        },
        {
          name: "OnePlus 11",
          brand: "OnePlus",
          price: 57999,
          image: "https://via.placeholder.com/300",
        },
        {
          name: "Sony WH-1000XM5",
          brand: "Sony",
          price: 29999,
          image: "https://via.placeholder.com/300",
        },
      ]);
      setLoading(false);
    }, 1500);
  }, []);

  return (
    <Container sx={{ py: 5 }}>
      <Typography variant="h4" mb={4} fontWeight="bold" align="center">
        Trending Products
      </Typography>

      {loading ? (
        <Loader text="Fetching products..." />
      ) : (
        <Grid container spacing={3} justifyContent="center">
          {productCards.map((product, index) => (
            <Grid item key={index}>
              <ProductCard product={product} />
            </Grid>
          ))}
        </Grid>
      )}
    </Container>
  );
};

export default HomePage;
