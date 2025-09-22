import React, { useState } from 'react';
import { Card, CardMedia, CardContent, Typography, Box, IconButton, Button, Chip, Rating, Tooltip } from '@mui/material';
import { FavoriteOutlined, Favorite, ShoppingCartOutlined, OpenInNew } from '@mui/icons-material';

const ProductCard = ({ product, onAddToCart, onAddToWishlist }) => {
  const [isInWishlist, setIsInWishlist] = useState(false);
  const [imageError, setImageError] = useState(false);

  const handleWishlistToggle = () => {
    setIsInWishlist(!isInWishlist);
    onAddToWishlist?.(product);
  };

  const formatPrice = (price) => (price ? `₹${price.toLocaleString('en-IN')}` : '₹0');

  const getPlatformColor = (platform) => {
    switch (platform?.toLowerCase()) {
      case 'amazon': return '#ff9900';
      case 'meesho': return '#f43397';
      default: return '#6366f1';
    }
  };

  return (
    <Card sx={{
      display: 'flex',
      flexDirection: 'column',
      borderRadius: 2,
      boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
      transition: '0.2s',
      '&:hover': { transform: 'translateY(-4px)', boxShadow: '0 8px 24px rgba(0,0,0,0.15)' }
    }}>
      <Box sx={{ position: 'relative' }}>
        <CardMedia
          component="img"
          height="200"
          image={imageError ? '/placeholder.png' : product.imageUrl || '/placeholder.png'}
          alt={product.title}
          sx={{ objectFit: 'contain', p: 1, backgroundColor: '#f5f5f5' }}
          onError={() => setImageError(true)}
        />
        <Chip
          label={product.platform || 'Unknown'}
          size="small"
          sx={{
            position: 'absolute', top: 8, left: 8,
            backgroundColor: getPlatformColor(product.platform),
            color: 'white', fontWeight: 'bold'
          }}
        />
        <IconButton
          onClick={handleWishlistToggle}
          sx={{ position: 'absolute', top: 8, right: 8, backgroundColor: 'white' }}
        >
          {isInWishlist ? <Favorite sx={{ color: '#ec4899' }} /> : <FavoriteOutlined />}
        </IconButton>
      </Box>

      <CardContent sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column' }}>
        <Tooltip title={product.title} arrow>
          <Typography variant="h6" sx={{
            fontWeight: 'bold', mb: 1,
            overflow: 'hidden', textOverflow: 'ellipsis',
            display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical',
            minHeight: '64px'
          }}>
            {product.title}
          </Typography>
        </Tooltip>

        {product.brand && product.brand !== 'Unknown' && (
          <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
            Brand: {product.brand}
          </Typography>
        )}

        {product.rating && (
          <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
            <Rating value={product.rating} precision={0.1} size="small" readOnly />
            {product.reviewCount && (
              <Typography variant="body2" sx={{ ml: 1 }}>({product.reviewCount})</Typography>
            )}
          </Box>
        )}

        <Typography variant="h5" sx={{ fontWeight: 'bold', color: '#6366f1', mt: 'auto' }}>
          {formatPrice(product.price)}
        </Typography>

        <Box sx={{ display: 'flex', gap: 1, mt: 2 }}>
          <Button
            variant="contained"
            fullWidth
            startIcon={<ShoppingCartOutlined />}
            onClick={() => onAddToCart?.(product)}
          >
            Add to Cart
          </Button>
          <Button variant="outlined" onClick={() => window.open(product.productUrl, '_blank')}>
            <OpenInNew />
          </Button>
        </Box>
      </CardContent>
    </Card>
  );
};

export default ProductCard;
