import React, { useEffect, useState } from 'react';
import {
  Box, Typography, Grid, Button, IconButton, Card, CardContent, CardMedia,
  Snackbar, Alert, Dialog, DialogActions, DialogContent, DialogContentText,
  DialogTitle, TextField, Switch, FormControlLabel, Chip, CircularProgress,
} from '@mui/material';
import {
  FavoriteOutlined, DeleteOutline, OpenInNew, NotificationsActive,
  TrendingDown, Edit, Save, Cancel,
} from '@mui/icons-material';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Wishlist = () => {
  const { user } = useAuth();
  const [wishlistItems, setWishlistItems] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
  const [deleteDialog, setDeleteDialog] = useState({ open: false, item: null });
  const [clearDialog, setClearDialog] = useState(false);
  const [editDialog, setEditDialog] = useState({ open: false, item: null, targetPrice: '', alertEnabled: false });
  const navigate = useNavigate();

  useEffect(() => {
    if (user) fetchWishlist();
  }, [user]);

  const fetchWishlist = async () => {
    setIsLoading(true);
    try {
      const response = await axios.get(`http://localhost:8080/api/wishlist/user/${user.id}`);
      console.log('✅ Wishlist data fetched:', response.data);
      setWishlistItems(response.data || []);
    } catch (error) {
      console.error('❌ Error fetching wishlist:', error);
      showSnackbar('Failed to fetch wishlist', 'error');
    } finally {
      setIsLoading(false);
    }
  };

  const removeFromWishlist = async (wishlistId) => {
    try {
      await axios.delete(`http://localhost:8080/api/wishlist/${wishlistId}`);
      setWishlistItems(prev => prev.filter(item => item.id !== wishlistId));
      showSnackbar('Item removed from wishlist', 'success');
    } catch (error) {
      console.error('❌ Error removing from wishlist:', error);
      showSnackbar('Failed to remove item', 'error');
    }
  };

  const clearWishlist = async () => {
    try {
      const promises = wishlistItems.map(item =>
        axios.delete(`http://localhost:8080/api/wishlist/${item.id}`)
      );
      await Promise.all(promises);
      setWishlistItems([]);
      showSnackbar('Wishlist cleared successfully', 'success');
    } catch (error) {
      console.error('❌ Error clearing wishlist:', error);
      showSnackbar('Failed to clear wishlist', 'error');
    }
  };

  const handleEditWishlist = (item) => {
    console.log('📝 Opening edit dialog for item:', item);
    setEditDialog({
      open: true,
      item,
      targetPrice: item.targetPrice || '',
      alertEnabled: item.alertEnabled || false,
    });
  };

  const updateWishlist = async () => {
    const { item, targetPrice, alertEnabled } = editDialog;

    if (!targetPrice || parseFloat(targetPrice) <= 0) {
      showSnackbar('Please enter a valid target price', 'error');
      return;
    }

    try {
      console.log('🔄 Updating wishlist:', {
        id: item.id,
        targetPrice: targetPrice,
        alertEnabled: alertEnabled
      });

      const response = await axios.put(
        `http://localhost:8080/api/wishlist/${item.id}?targetPrice=${targetPrice}&alertEnabled=${alertEnabled}`
      );

      console.log('✅ Update response:', response.data);

      // Update local state immediately with server response
      setWishlistItems(prev =>
        prev.map(w => w.id === item.id ? response.data : w)
      );

      const alertStatus = response.data.alertEnabled ? 'ENABLED ✅' : 'DISABLED ⛔';
      showSnackbar(`Alert ${alertStatus} | Target: ₹${targetPrice}`, 'success');
      
      setEditDialog({ open: false, item: null, targetPrice: '', alertEnabled: false });
      
      // Refresh wishlist after a short delay to confirm persistence
      setTimeout(() => {
        console.log('🔄 Refreshing wishlist to confirm changes...');
        fetchWishlist();
      }, 800);
    } catch (error) {
      console.error('❌ Error updating wishlist:', error);
      showSnackbar('Failed to update wishlist. Please try again.', 'error');
    }
  };

  const handleViewDetails = (productUrl) => {
    if (productUrl) {
      window.open(productUrl, '_blank', 'noopener,noreferrer');
    } else {
      showSnackbar('No product link available', 'warning');
    }
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
    if (deleteDialog.item) removeFromWishlist(deleteDialog.item.id);
    setDeleteDialog({ open: false, item: null });
  };

  const handleClearConfirm = () => setClearDialog(true);
  
  const confirmClear = () => {
    clearWishlist();
    setClearDialog(false);
  };

  if (!user) {
    return (
      <Box sx={{ p: 4, textAlign: 'center' }}>
        <FavoriteOutlined sx={{ fontSize: 100, color: 'text.secondary', mb: 2 }} />
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
          sx={{
            backgroundColor: '#6366f1',
            '&:hover': { backgroundColor: '#5b21b6' },
          }}
        >
          Go to Login
        </Button>
      </Box>
    );
  }

  if (isLoading) {
    return (
      <Box sx={{ p: 4, display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '60vh' }}>
        <Box sx={{ textAlign: 'center' }}>
          <CircularProgress size={60} />
          <Typography variant="h6" sx={{ mt: 3, color: 'text.secondary' }}>
            Loading your wishlist...
          </Typography>
        </Box>
      </Box>
    );
  }

  if (wishlistItems.length === 0) {
    return (
      <Box sx={{ p: 4, textAlign: 'center' }}>
        <FavoriteOutlined sx={{ fontSize: 100, color: 'text.secondary', mb: 2 }} />
        <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 2 }}>
          Your wishlist is empty
        </Typography>
        <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
          Save items you love to your wishlist and get price drop alerts
        </Typography>
        <Button
          variant="contained"
          size="large"
          onClick={() => navigate('/products')}
          sx={{
            backgroundColor: '#ec4899',
            '&:hover': { backgroundColor: '#db2777' },
          }}
        >
          Explore Products
        </Button>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 4 }}>
      {/* Header */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 1 }}>
            My Wishlist
          </Typography>
          <Typography variant="body2" color="text.secondary">
            {wishlistItems.length} item{wishlistItems.length !== 1 ? 's' : ''} saved for later
          </Typography>
          <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mt: 0.5 }}>
            {wishlistItems.filter(item => item.alertEnabled).length} alert{wishlistItems.filter(item => item.alertEnabled).length !== 1 ? 's' : ''} active
          </Typography>
        </Box>
        <Button
          variant="outlined"
          color="error"
          startIcon={<DeleteOutline />}
          onClick={handleClearConfirm}
        >
          Clear Wishlist
        </Button>
      </Box>

      {/* Wishlist Items Grid */}
      <Grid container spacing={3}>
        {wishlistItems.map((item) => (
          <Grid item xs={12} sm={6} md={4} lg={3} key={item.id}>
            <Card
              sx={{
                position: 'relative',
                borderRadius: 2,
                boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
                transition: 'all 0.3s',
                '&:hover': { 
                  transform: 'translateY(-5px)',
                  boxShadow: '0 4px 16px rgba(0,0,0,0.15)',
                },
              }}
            >
              {/* Delete Button */}
              <IconButton
                onClick={() => handleDeleteConfirm(item)}
                sx={{
                  position: 'absolute',
                  top: 8,
                  right: 8,
                  backgroundColor: 'rgba(255,255,255,0.9)',
                  color: 'error.main',
                  zIndex: 1,
                  '&:hover': { backgroundColor: 'rgba(255,255,255,1)' },
                }}
              >
                <DeleteOutline />
              </IconButton>

              {/* Alert Badge */}
              {item.alertEnabled && (
                <Chip
                  icon={<NotificationsActive />}
                  label="Alert ON"
                  color="success"
                  size="small"
                  sx={{ position: 'absolute', top: 8, left: 8, zIndex: 1 }}
                />
              )}

              {/* Product Image */}
              <CardMedia
                component="img"
                height="200"
                image={item.product?.imageUrl || '/placeholder.png'}
                alt={item.product?.title || 'Product'}
                sx={{ objectFit: 'contain', backgroundColor: '#f9f9f9', p: 2 }}
              />

              {/* Product Details */}
              <CardContent sx={{ p: 2 }}>
                <Typography
                  variant="subtitle1"
                  sx={{
                    fontWeight: 'bold',
                    mb: 1,
                    overflow: 'hidden',
                    textOverflow: 'ellipsis',
                    display: '-webkit-box',
                    WebkitLineClamp: 2,
                    WebkitBoxOrient: 'vertical',
                    minHeight: '48px',
                  }}
                >
                  {item.product?.title || 'Unknown Product'}
                </Typography>

                <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                  {item.product?.platform || 'Unknown Platform'}
                </Typography>

                {/* Price Information */}
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2, flexWrap: 'wrap' }}>
                  <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#6366f1' }}>
                    ₹{(item.product?.price || item.product?.currentPrice || 0).toLocaleString()}
                  </Typography>
                  {item.targetPrice && (
                    <>
                      <TrendingDown sx={{ fontSize: 18, color: '#10b981' }} />
                      <Typography variant="body2" color="success.main">
                        Target: ₹{parseFloat(item.targetPrice).toLocaleString()}
                      </Typography>
                    </>
                  )}
                </Box>

                {/* Target Met Badge */}
                {item.targetPrice && (item.product?.price || item.product?.currentPrice) <= item.targetPrice && (
                  <Chip
                    label="🎯 Target Price Met!"
                    color="success"
                    size="small"
                    sx={{ mb: 1, width: '100%' }}
                  />
                )}

                {/* Action Buttons */}
                <Button
                  variant="contained"
                  size="small"
                  fullWidth
                  startIcon={<Edit />}
                  onClick={() => handleEditWishlist(item)}
                  sx={{
                    backgroundColor: '#f59e0b',
                    '&:hover': { backgroundColor: '#d97706' },
                    mb: 1,
                  }}
                >
                  {item.alertEnabled ? 'Edit Alert' : 'Set Alert'}
                </Button>

                <Button
                  variant="outlined"
                  size="small"
                  fullWidth
                  startIcon={<OpenInNew />}
                  onClick={() => handleViewDetails(item.product?.productUrl || item.product?.platformProductUrl)}
                >
                  View Product
                </Button>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      {/* Edit Wishlist Dialog */}
      <Dialog
        open={editDialog.open}
        onClose={() => setEditDialog({ open: false, item: null, targetPrice: '', alertEnabled: false })}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <Edit color="primary" />
            Set Price Alert
          </Box>
        </DialogTitle>
        <DialogContent>
          <DialogContentText sx={{ mb: 2 }}>
            Get notified via email when the price drops below your target price.
          </DialogContentText>

          <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
            Current Price: <strong style={{ color: '#6366f1', fontSize: '1.1em' }}>
              ₹{(editDialog.item?.product?.price || editDialog.item?.product?.currentPrice || 0).toLocaleString()}
            </strong>
          </Typography>

          <TextField
            autoFocus
            margin="dense"
            label="Target Price (₹)"
            type="number"
            fullWidth
            variant="outlined"
            value={editDialog.targetPrice}
            onChange={(e) => setEditDialog(prev => ({ ...prev, targetPrice: e.target.value }))}
            placeholder="Enter your target price"
            sx={{ mb: 2 }}
            inputProps={{ min: 0, step: 1 }}
          />

          <FormControlLabel
            control={
              <Switch
                checked={editDialog.alertEnabled}
                onChange={(e) => setEditDialog(prev => ({ ...prev, alertEnabled: e.target.checked }))}
                color="success"
              />
            }
            label={
              <Box>
                <Typography variant="body2" sx={{ fontWeight: 'bold' }}>
                  Enable email alerts for price drops
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  You'll receive notifications every 6 hours when price drops below target
                </Typography>
              </Box>
            }
          />
        </DialogContent>
        <DialogActions sx={{ p: 2 }}>
          <Button
            onClick={() => setEditDialog({ open: false, item: null, targetPrice: '', alertEnabled: false })}
            startIcon={<Cancel />}
          >
            Cancel
          </Button>
          <Button
            onClick={updateWishlist}
            variant="contained"
            startIcon={<Save />}
            sx={{ backgroundColor: '#6366f1', '&:hover': { backgroundColor: '#5b21b6' } }}
          >
            Save Alert
          </Button>
        </DialogActions>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <Dialog
        open={deleteDialog.open}
        onClose={() => setDeleteDialog({ open: false, item: null })}
      >
        <DialogTitle>Remove from Wishlist</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to remove <strong>"{deleteDialog.item?.product?.title}"</strong> from your wishlist?
            {deleteDialog.item?.alertEnabled && (
              <Typography variant="body2" color="warning.main" sx={{ mt: 1 }}>
                ⚠️ This will also disable the price alert for this product.
              </Typography>
            )}
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteDialog({ open: false, item: null })}>
            Cancel
          </Button>
          <Button onClick={confirmDelete} color="error" variant="contained">
            Remove
          </Button>
        </DialogActions>
      </Dialog>

      {/* Clear Wishlist Dialog */}
      <Dialog open={clearDialog} onClose={() => setClearDialog(false)}>
        <DialogTitle>Clear Entire Wishlist</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to remove all <strong>{wishlistItems.length}</strong> items from your wishlist? 
            This action cannot be undone.
            {wishlistItems.filter(item => item.alertEnabled).length > 0 && (
              <Typography variant="body2" color="warning.main" sx={{ mt: 2 }}>
                ⚠️ This will also disable {wishlistItems.filter(item => item.alertEnabled).length} active price alert(s).
              </Typography>
            )}
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setClearDialog(false)}>Cancel</Button>
          <Button onClick={confirmClear} color="error" variant="contained">
            Clear All
          </Button>
        </DialogActions>
      </Dialog>

      {/* Snackbar Notifications */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={4000}
        onClose={handleCloseSnackbar}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert
          onClose={handleCloseSnackbar}
          severity={snackbar.severity}
          sx={{ width: '100%' }}
          variant="filled"
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default Wishlist;