import React, { useEffect, useState } from 'react';
import {
  Box, 
  Typography, 
  Grid, 
  Button, 
  IconButton,
  Card,
  CardContent,
  CardMedia,
  Snackbar,
  Alert,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from '@mui/material';
import {
  FavoriteOutlined, 
  DeleteOutline,
  ShoppingCartOutlined,
  OpenInNew,
} from '@mui/icons-material';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Wishlist = () => {
  const { user, token } = useAuth();
  const [wishlistItems, setWishlistItems] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
  const [deleteDialog, setDeleteDialog] = useState({ open: false, item: null });
  const [clearDialog, setClearDialog] = useState(false);
  const navigate = useNavigate();

  // Fetch wishlist from backend
  useEffect(() => {
    fetchWishlist();
  }, [user, token]);

  const fetchWishlist = async () => {
    if (!user) {
      navigate('/login');
      return;
    }

    setIsLoading(true);
    try {
      const response = await axios.get(
        `http://localhost:8080/api/wishlist/${user.id}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setWishlistItems(response.data || []);
    } catch (error) {
      console.error('Error fetching wishlist:', error);
      showSnackbar('Failed to fetch wishlist', 'error');
      setWishlistItems([]);
    } finally {
      setIsLoading(false);
    }
  };

  const removeFromWishlist = async (wishlistId) => {
    try {
      await axios.delete(
        `http://localhost:8080/api/wishlist/${user.id}/remove/${wishlistId}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setWishlistItems(prev => prev.filter(item => item.id !== wishlistId));
      showSnackbar('Item removed from wishlist', 'success');
    } catch (error) {
      console.error('Error removing from wishlist:', error);
      showSnackbar('Failed to remove item', 'error');
    }
  };

  const clearWishlist = async () => {
    try {
      const promises = wishlistItems.map(item => 
        axios.delete(
          `http://localhost:8080/api/wishlist/${user.id}/remove/${item.id}`,
          { headers: { Authorization: `Bearer ${token}` } }
        )
      );
      await Promise.all(promises);
      setWishlistItems([]);
      showSnackbar('Wishlist cleared successfully', 'success');
    } catch (error) {
      console.error('Error clearing wishlist:', error);
      showSnackbar('Failed to clear wishlist', 'error');
    }
  };

  const handleAddToCart = (item) => {
    console.log('Add to cart:', item);
    showSnackbar('Product added to cart!', 'success');
  };

  const handleViewDetails = (productUrl) => {
    window.open(productUrl, '_blank', 'noopener,noreferrer');
  };

  const showSnackbar = (message, severity = 'success') => {
    setSnackbar({ open: true, message, severity });
  };

  const handleCloseSnackbar = () => {
    setSnackbar(prev => ({ ...prev, open: false }));
  };

  const handleDeleteConfirm = (item) => {
    setDeleteDialog({ open: true, item });
  };

  const confirmDelete = () => {
    if (deleteDialog.item) {
      removeFromWishlist(deleteDialog.item.id);
    }
    setDeleteDialog({ open: false, item: null });
  };

  const handleClearConfirm = () => {
    setClearDialog(true);
  };

  const confirmClear = () => {
    clearWishlist();
    setClearDialog(false);
  };

  if (!user) {
    return (
      <Box sx={{ p: 4, textAlign: 'center' }}>
        <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 2 }}>
          Please Login
        </Typography>
        <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
          You need to be logged in to view your wishlist
        </Typography>
        <Button
          variant="contained"
          size="large"
          onClick={() => navigate('/login')}
          sx={{ backgroundColor: '#6366f1', '&:hover': { backgroundColor: '#5b21b6' } }}
        >
          Go to Login
        </Button>
      </Box>
    );
  }

  if (isLoading) {
    return (
      <Box sx={{ p: 4, textAlign: 'center' }}>
        <Typography variant="h6" color="text.secondary">
          Loading your wishlist...
        </Typography>
      </Box>
    );
  }

  if (wishlistItems.length === 0) {
    return (
      <Box sx={{ p: 4, textAlign: 'center' }}>
        <FavoriteOutlined sx={{ fontSize: 120, color: 'text.secondary', mb: 2 }} />
        <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 2 }}>
          Your wishlist is empty
        </Typography>
        <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
          Save items you love to your wishlist and never lose track of them
        </Typography>
        <Button
          variant="contained"
          size="large"
          onClick={() => navigate('/products')}
          sx={{ backgroundColor: '#ec4899', '&:hover': { backgroundColor: '#db2777' } }}
        >
          Explore Products
        </Button>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 4 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Box>
          <Typography variant="h3" sx={{ fontWeight: 'bold', mb: 1 }}>
            My Wishlist
          </Typography>
          <Typography variant="body1" color="text.secondary">
            {wishlistItems.length} item{wishlistItems.length !== 1 ? 's' : ''} saved for later
          </Typography>
        </Box>
        <Button
          variant="outlined"
          color="error"
          startIcon={<DeleteOutline />}
          onClick={handleClearConfirm}
          disabled={wishlistItems.length === 0}
        >
          Clear Wishlist
        </Button>
      </Box>

      <Grid container spacing={3}>
        {wishlistItems.map((item) => (
          <Grid item xs={12} sm={6} md={4} lg={3} key={item.id}>
            <Card 
              sx={{ 
                position: 'relative',
                borderRadius: 2, 
                boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
                transition: 'transform 0.2s, box-shadow 0.2s',
                '&:hover': {
                  transform: 'translateY(-2px)',
                  boxShadow: '0 4px 16px rgba(0,0,0,0.15)',
                },
              }}
            >
              <IconButton
                onClick={() => handleDeleteConfirm(item)}
                sx={{
                  position: 'absolute',
                  top: 8,
                  left: 8,
                  backgroundColor: 'rgba(255,255,255,0.9)',
                  color: 'error.main',
                  zIndex: 1,
                  '&:hover': { 
                    backgroundColor: 'rgba(255,255,255,1)',
                    color: 'error.dark',
                  },
                }}
              >
                <DeleteOutline />
              </IconButton>

              <CardMedia
                component="img"
                height="200"
                image={item.imageUrl || '/placeholder.png'}
                alt={item.title}
                sx={{ objectFit: 'cover' }}
              />

              <CardContent>
                <Typography
                  variant="h6"
                  sx={{
                    fontWeight: 'bold',
                    mb: 1,
                    overflow: 'hidden',
                    textOverflow: 'ellipsis',
                    whiteSpace: 'nowrap',
                  }}
                >
                  {item.title}
                </Typography>

                {item.brand && (
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                    {item.brand}
                  </Typography>
                )}

                <Typography variant="h5" sx={{ fontWeight: 'bold', color: '#6366f1', mb: 2 }}>
                  ₹{item.price?.toLocaleString() || '0'}
                </Typography>

                {item.rating && (
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                    Rating: {item.rating} ⭐ ({item.reviewCount || 0} reviews)
                  </Typography>
                )}

                <Button
                  variant="contained"
                  fullWidth
                  startIcon={<ShoppingCartOutlined />}
                  onClick={() => handleAddToCart(item)}
                  sx={{
                    backgroundColor: '#6366f1',
                    '&:hover': { backgroundColor: '#5b21b6' },
                    mb: 1,
                  }}
                >
                  Add to Cart
                </Button>

                <Button
                  variant="outlined"
                  fullWidth
                  startIcon={<OpenInNew />}
                  onClick={() => handleViewDetails(item.productUrl)}
                >
                  View Details
                </Button>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      {/* Delete confirmation dialog */}
      <Dialog
        open={deleteDialog.open}
        onClose={() => setDeleteDialog({ open: false, item: null })}
      >
        <DialogTitle>Remove from Wishlist</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to remove "{deleteDialog.item?.title}" from your wishlist?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteDialog({ open: false, item: null })}>
            Cancel
          </Button>
          <Button onClick={confirmDelete} color="error">
            Remove
          </Button>
        </DialogActions>
      </Dialog>

      {/* Clear wishlist confirmation dialog */}
      <Dialog
        open={clearDialog}
        onClose={() => setClearDialog(false)}
      >
        <DialogTitle>Clear Wishlist</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to remove all items from your wishlist? This action cannot be undone.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setClearDialog(false)}>
            Cancel
          </Button>
          <Button onClick={confirmClear} color="error">
            Clear All
          </Button>
        </DialogActions>
      </Dialog>

      <Snackbar
        open={snackbar.open}
        autoHideDuration={6000}
        onClose={handleCloseSnackbar}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'left' }}
      >
        <Alert onClose={handleCloseSnackbar} severity={snackbar.severity} sx={{ width: '100%' }}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default Wishlist;
