import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Grid,
  TextField,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Chip,
  InputAdornment,
  Paper,
  Button,
  CircularProgress,
  Snackbar,
  Alert,
  Tabs,
  Tab,
  Pagination,
} from '@mui/material';
import { Search, FilterList } from '@mui/icons-material';
import axios from 'axios';
import ProductCard from '../components/ProductCard';

const sortOptions = [
  { value: 'name', label: 'Name' },
  { value: 'price-low', label: 'Price: Low to High' },
  { value: 'price-high', label: 'Price: High to Low' },
  { value: 'rating', label: 'Highest Rated' },
];

const Products = () => {
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedPlatform, setSelectedPlatform] = useState('all');
  const [sortBy, setSortBy] = useState('name');
  const [products, setProducts] = useState([]);    
  const [filteredProducts, setFilteredProducts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [activeTab, setActiveTab] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [productsPerPage] = useState(20);

  const fetchProducts = async () => {
    console.log("hit")
    if (!searchQuery.trim()) {
      setProducts([]);
      setError('Please enter a search term');
      return;
    }

    setLoading(true);
    setError(null);
    setCurrentPage(1);

    try {
      const response = await axios.get('http://localhost:8080/api/products/search', {
        params: { query: searchQuery },
        // timeout: 30000,
      });

      const searchResult = response.data?.data;

      if (searchResult?.all?.length > 0) {
        setProducts(searchResult.all);
      } else {
        setProducts([]);
        setError('No products found. Try a different search term.');
      }
    } catch (err) {
      console.error('Error fetching products:', err);
      setError(err.response?.data?.message || 'Failed to fetch products.');
    } finally {
      setLoading(false);
    }
  };

  // Apply filtering + sorting
  useEffect(() => {
    let filtered = [...products];

    if (selectedPlatform !== 'all') {
      filtered = filtered.filter(
        (product) => product.platform?.toLowerCase() === selectedPlatform
      );
    }

    filtered.sort((a, b) => {
      switch (sortBy) {
        case 'price-low':
          return (a.price || 0) - (b.price || 0);
        case 'price-high':
          return (b.price || 0) - (a.price || 0);
        case 'rating':
          return (b.rating || 0) - (a.rating || 0);
        default:
          return a.title?.localeCompare(b.title || '');
      }
    });

    setFilteredProducts(filtered);
  }, [products, selectedPlatform, sortBy]);

  // Pagination
  const indexOfLastProduct = currentPage * productsPerPage;
  const currentProducts = filteredProducts.slice(
    indexOfLastProduct - productsPerPage,
    indexOfLastProduct
  );
  const totalPages = Math.ceil(filteredProducts.length / productsPerPage);

  const handleTabChange = (event, newValue) => {
    setActiveTab(newValue);
    setSelectedPlatform(newValue === 0 ? 'all' : newValue === 1 ? 'amazon' : 'meesho');
  };

  


  return (
    <Box sx={{ p: 4 }}>
      <Typography variant="h3" sx={{ fontWeight: 'bold', mb: 1 }}>
        Products
      </Typography>
      <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
        Discover and compare prices across multiple platforms
      </Typography>

      {/* Platform Tabs */}
      {/* <Box sx={{ borderBottom: 1, borderColor: 'divider', mb: 3 }}>
        <Tabs value={activeTab} onChange={handleTabChange}>
          <Tab label="All Platforms" />
           <Tab label="Amazon" />
          <Tab label="Meesho" /> 
        </Tabs>
      </Box> */}

      {/* Search & Sort */}
      <Paper elevation={1} sx={{ p: 3, mb: 4, borderRadius: 2 }}>
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              placeholder="Search products..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && fetchProducts()}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <Search />
                  </InputAdornment>
                ),
              }}
              sx={{ '& .MuiOutlinedInput-root': { borderRadius: 2 } }}
            />
          </Grid>
          <Grid item xs={12} md={2}>
            <Button
              fullWidth
              variant="contained"
              onClick={fetchProducts}
              disabled={loading}
              sx={{ height: '56px', borderRadius: 2 }}
            >
              {loading ? <CircularProgress size={24} /> : 'Search'}
            </Button>
          </Grid>
          <Grid item xs={12} md={2}>
            <FormControl fullWidth>
              <InputLabel>Sort By</InputLabel>
              <Select value={sortBy} onChange={(e) => setSortBy(e.target.value)} label="Sort By">
                {sortOptions.map((option) => (
                  <MenuItem key={option.value} value={option.value}>
                    {option.label}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={12} md={2}>
            <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
              <FilterList sx={{ mr: 1, color: 'text.secondary' }} />
              <Chip label={`${filteredProducts.length} Products`} color="primary" variant="outlined" />
            </Box>
          </Grid>
        </Grid>
      </Paper>

      {/* Loading */}
      {loading && (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
          <CircularProgress />
          <Typography variant="h6" sx={{ ml: 2 }}>
            Fetching products...
          </Typography>
        </Box>
      )}

      {/* Products Grid */}
      {!loading && currentProducts.length > 0 && (
        <>
          <Typography variant="h6" sx={{ mb: 2 }}>
            Showing {indexOfLastProduct - productsPerPage + 1}-
            {Math.min(indexOfLastProduct, filteredProducts.length)} of {filteredProducts.length}{' '}
            products
          </Typography>

          <Grid container spacing={2}>
            {currentProducts.map((product, i) => (
              <Grid item xs={12} sm={6} md={4} lg={3} key={i}>
                <ProductCard product={product} />
              </Grid>
            ))}
          </Grid>

          {totalPages > 1 && (
            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
              <Pagination
                count={totalPages}
                page={currentPage}
                onChange={(e, val) => setCurrentPage(val)}
                color="primary"
                size="large"
              />
            </Box>
          )}
        </>
      )}

      {/* Empty State */}
      {!loading && filteredProducts.length === 0 && !error && (
        <Box sx={{ textAlign: 'center', py: 8 }}>
          <Typography variant="h6" color="text.secondary">
            Search for products to see results
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Enter a product name above and click search
          </Typography>
        </Box>
      )}

      {/* Error Snackbar */}
      <Snackbar
        open={!!error}
        autoHideDuration={6000}
        onClose={() => setError(null)}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert onClose={() => setError(null)} severity="error">
          {error}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default Products;
