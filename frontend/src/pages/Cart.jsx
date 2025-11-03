import React, { useState } from 'react';
import {
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  CardMedia,
  IconButton,
  Button,
  Divider,
  TextField,
  Paper,
  Chip,
} from '@mui/material';
import {
  Add,
  Remove,
  DeleteOutline,
  ShoppingBagOutlined,
} from '@mui/icons-material';

const mockCartItems = [
  {
    id: 1,
    name: 'iPhone 15 Pro',
    image: 'https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=200',
    price: 129900,
    quantity: 1,
    originalPrice: 134900,
  },
  {
    id: 2,
    name: 'Sony WH-1000XM5',
    image: 'https://images.unsplash.com/photo-1484704849700-f032a568e944?w=200',
    price: 29999,
    quantity: 2,
    originalPrice: 32999,
  },
];

const Cart = () => {
  const [cartItems, setCartItems] = useState(mockCartItems);

  const updateQuantity = (id, newQuantity) => {
    if (newQuantity === 0) {
      setCartItems(cartItems.filter(item => item.id !== id));
    } else {
      setCartItems(cartItems.map(item =>
        item.id === id ? { ...item, quantity: newQuantity } : item
      ));
    }
  };

  const removeItem = (id) => {
    setCartItems(cartItems.filter(item => item.id !== id));
  };

  const subtotal = cartItems.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  const originalTotal = cartItems.reduce((sum, item) => sum + (item.originalPrice * item.quantity), 0);
  const totalSavings = originalTotal - subtotal;
  const shipping = 0; // Free shipping
  const total = subtotal + shipping;

  if (cartItems.length === 0) {
    return (
      <Box sx={{ p: 4, textAlign: 'center' }}>
        <ShoppingBagOutlined sx={{ fontSize: 120, color: 'text.secondary', mb: 2 }} />
        <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 2 }}>
          Your cart is empty
        </Typography>
        <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
          Looks like you haven't added anything to your cart yet
        </Typography>
        <Button
          variant="contained"
          size="large"
          sx={{
            backgroundColor: '#6366f1',
            '&:hover': {
              backgroundColor: '#5b21b6',
            },
          }}
        >
          Continue Shopping
        </Button>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 4 }}>
      <Typography variant="h3" sx={{ fontWeight: 'bold', mb: 1 }}>
        Shopping Cart
      </Typography>
      <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
        {cartItems.length} item{cartItems.length !== 1 ? 's' : ''} in your cart
      </Typography>

      <Grid container spacing={4}>
        <Grid item xs={12} md={8}>
          {cartItems.map((item) => (
            <Card key={item.id} sx={{ mb: 2, borderRadius: 2 }}>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                  <CardMedia
                    component="img"
                    sx={{ width: 100, height: 100, borderRadius: 2, mr: 3 }}
                    image={item.image}
                    alt={item.name}
                  />
                  <Box sx={{ flexGrow: 1 }}>
                    <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 1 }}>
                      {item.name}
                    </Typography>
                    <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                      <Typography
                        variant="h6"
                        sx={{ fontWeight: 'bold', color: '#6366f1', mr: 2 }}
                      >
                        ₹{item.price.toLocaleString()}
                      </Typography>
                      <Typography
                        variant="body2"
                        sx={{
                          textDecoration: 'line-through',
                          color: 'text.secondary',
                        }}
                      >
                        ₹{item.originalPrice.toLocaleString()}
                      </Typography>
                      <Chip
                        label={`₹${(item.originalPrice - item.price).toLocaleString()} saved`}
                        color="success"
                        size="small"
                        sx={{ ml: 2 }}
                      />
                    </Box>
                    <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                      <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <IconButton
                          onClick={() => updateQuantity(item.id, item.quantity - 1)}
                          size="small"
                        >
                          <Remove />
                        </IconButton>
                        <TextField
                          value={item.quantity}
                          onChange={(e) => updateQuantity(item.id, parseInt(e.target.value) || 1)}
                          size="small"
                          sx={{ width: 60, mx: 1 }}
                        />
                        <IconButton
                          onClick={() => updateQuantity(item.id, item.quantity + 1)}
                          size="small"
                        >
                          <Add />
                        </IconButton>
                      </Box>
                      <IconButton
                        onClick={() => removeItem(item.id)}
                        color="error"
                      >
                        <DeleteOutline />
                      </IconButton>
                    </Box>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          ))}
        </Grid>

        <Grid item xs={12} md={4}>
          <Paper elevation={3} sx={{ p: 3, borderRadius: 2, position: 'sticky', top: 20 }}>
            <Typography variant="h5" sx={{ fontWeight: 'bold', mb: 3 }}>
              Order Summary
            </Typography>

            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
              <Typography variant="body1">Subtotal</Typography>
              <Typography variant="body1">₹{subtotal.toLocaleString()}</Typography>
            </Box>

            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
              <Typography variant="body1">Original Price</Typography>
              <Typography
                variant="body1"
                sx={{ textDecoration: 'line-through', color: 'text.secondary' }}
              >
                ₹{originalTotal.toLocaleString()}
              </Typography>
            </Box>

            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
              <Typography variant="body1" sx={{ color: 'success.main', fontWeight: 'bold' }}>
                Total Savings
              </Typography>
              <Typography variant="body1" sx={{ color: 'success.main', fontWeight: 'bold' }}>
                ₹{totalSavings.toLocaleString()}
              </Typography>
            </Box>

            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
              <Typography variant="body1">Shipping</Typography>
              <Typography variant="body1" sx={{ color: 'success.main' }}>
                Free
              </Typography>
            </Box>

            <Divider sx={{ my: 2 }} />

            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
              <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                Total
              </Typography>
              <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                ₹{total.toLocaleString()}
              </Typography>
            </Box>

            <Button
              fullWidth
              variant="contained"
              size="large"
              sx={{
                py: 1.5,
                backgroundColor: '#6366f1',
                '&:hover': {
                  backgroundColor: '#5b21b6',
                },
              }}
            >
              Proceed to Checkout
            </Button>

            <Typography variant="caption" color="text.secondary" sx={{ display: 'block', textAlign: 'center', mt: 2 }}>
              Secure checkout with end-to-end encryption
            </Typography>
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
};

export default Cart;