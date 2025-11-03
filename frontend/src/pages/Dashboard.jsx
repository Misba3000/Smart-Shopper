import React, { useState } from 'react';
import {
  Box,
  Typography,
  TextField,
  Button,
  Grid,
  InputAdornment,
} from '@mui/material';
import { Search } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';

// ✅ Local product images
import Amazon from '../../public/product/amazon.jfif';
import Meesho from '../../public/product/meesho.jpg';
import Myntra from '../../public/product/myntra.jfif';
import ama2 from '../../public/product/ama2.jfif';

const mockProducts = [
  { id: 1, image: Amazon  },
  { id: 2, image: Myntra },
  { id: 3, image: Meesho },
  { id: 4, image: ama2 },
];

const Dashboard = () => {
  const [searchQuery, setSearchQuery] = useState('');
  const navigate = useNavigate();

  const handleSearch = () => {
    if (searchQuery.trim() !== '') {
      navigate(`/products?search=${searchQuery}`);
    }
  };

  return (
    <Box sx={{ p: 4 }}>
      {/* Header */}
      <Box sx={{ textAlign: 'center', mb: 6 }}>
        <Typography
          variant="h2"
          sx={{
            background: 'linear-gradient(45deg, #6366f1, #ec4899)',
            backgroundClip: 'text',
            WebkitBackgroundClip: 'text',
            color: 'transparent',
            fontWeight: 'bold',
            mb: 2,
          }}
        >
          Smart Price Comparison
        </Typography>
        <Typography variant="h6" color="text.secondary" sx={{ mb: 4 }}>
          Compare prices across multiple platforms, track price history, and get alerts when
          prices drop
        </Typography>

        {/* Search Bar */}
        <TextField
          fullWidth
          placeholder="Search for products, brands, or categories..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <Search />
              </InputAdornment>
            ),
            endAdornment: (
              <InputAdornment position="end">
                <Button
                  variant="contained"
                  sx={{
                    backgroundColor: '#6366f1',
                    '&:hover': {
                      backgroundColor: '#5b21b6',
                    },
                  }}
                  onClick={handleSearch}
                >
                  Search
                </Button>
              </InputAdornment>
            ),
          }}
          sx={{
            maxWidth: 600,
            '& .MuiOutlinedInput-root': {
              borderRadius: 3,
            },
          }}
        />
      </Box>

      {/* Best Deals */}
      <Box sx={{ textAlign: 'center' }}>
        <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 3 }}>
          Best Deals
        </Typography>

        <Grid container spacing={3} justifyContent="center">
          {mockProducts.map((product) => (
            <Grid item xs={12} sm={6} md={4} key={product.id}>
              <Box
                component="img"
                src={product.image}
                alt={product.name}
                sx={{
                  width: '100%',
                  height: 250,
                  objectFit: 'cover',
                  borderRadius: 4,
                  boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
                  transition: 'transform 0.2s',
                  '&:hover': {
                    transform: 'scale(1.05)',
                  },
                }}
              />
              <Typography variant="h6" sx={{ mt: 1 }}>
                {product.name}
              </Typography>
            </Grid>
          ))}
        </Grid>
      </Box>
    </Box>
  );
};

export default Dashboard;
