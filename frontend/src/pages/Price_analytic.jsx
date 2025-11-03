import React, { useEffect, useState } from 'react';
import {
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  Paper,
  Avatar,
  Chip,
  CircularProgress,
  Button,
  Snackbar,
  Alert,
  Tooltip,
} from '@mui/material';
import {
  TrendingUp,
  TrendingDown,
  ShowChart,
  Timeline,
  PriceChange,
  Analytics,
  Refresh,
  CheckCircle,
  ErrorOutline,
} from '@mui/icons-material';
import axios from 'axios';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

const PriceAnalytics = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [analyticsData, setAnalyticsData] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
  const [analytics, setAnalytics] = useState({
    totalProducts: 0,
    activeAlerts: 0,
    averageSavings: 0,
    priceDropsDetected: 0,
  });

  useEffect(() => {
    if (user) {
      fetchAnalyticsData();
    }
  }, [user]);

  const fetchAnalyticsData = async () => {
    setIsLoading(true);
    try {
      // Fetch analytics data from backend
      const response = await axios.get(`http://localhost:8080/api/analytics/user/${user.id}`);
      const data = response.data || [];
      setAnalyticsData(data);

      // Calculate summary analytics
      calculateSummaryAnalytics(data);
      
      showSnackbar('Analytics data loaded successfully', 'success');
    } catch (error) {
      console.error('Error fetching analytics:', error);
      showSnackbar('Failed to load analytics data', 'error');
      
      // Fallback: try to fetch regular wishlist data
      try {
        const fallbackResponse = await axios.get(`http://localhost:8080/api/wishlist/user/${user.id}`);
        const fallbackData = fallbackResponse.data || [];
        const alertedItems = fallbackData.filter(item => item.alertEnabled);
        
        // Convert wishlist format to analytics format
        const convertedData = alertedItems.map(item => ({
          productId: item.product?.id,
          productTitle: item.product?.title,
          platform: item.product?.platform,
          currentPrice: item.product?.price || item.product?.currentPrice,
          targetPrice: item.targetPrice,
          lowestPrice: (item.product?.price || item.product?.currentPrice) * 0.9,
          highestPrice: (item.product?.price || item.product?.currentPrice) * 1.1,
          averagePrice: item.product?.price || item.product?.currentPrice,
          priceChange: 0,
          priceChangePercentage: 0,
          trend: 'stable',
          alertEnabled: item.alertEnabled,
          lastTracked: item.createdAt,
          priceHistory: []
        }));
        
        setAnalyticsData(convertedData);
        calculateSummaryAnalytics(convertedData);
      } catch (fallbackError) {
        console.error('Fallback failed:', fallbackError);
      }
    } finally {
      setIsLoading(false);
    }
  };

  const calculateSummaryAnalytics = (data) => {
    const totalProducts = data.length;
    const activeAlerts = data.filter(item => item.alertEnabled).length;

    // Calculate potential savings
    const totalSavings = data.reduce((sum, item) => {
      if (item.targetPrice && item.currentPrice) {
        const currentPrice = parseFloat(item.currentPrice);
        const targetPrice = parseFloat(item.targetPrice);
        if (currentPrice > targetPrice) {
          return sum + (currentPrice - targetPrice);
        }
      }
      return sum;
    }, 0);

    const averageSavings = totalProducts > 0 ? Math.round(totalSavings / totalProducts) : 0;

    // Count products where target is met
    const priceDropsDetected = data.filter(item => {
      if (item.targetPrice && item.currentPrice) {
        return parseFloat(item.currentPrice) <= parseFloat(item.targetPrice);
      }
      return false;
    }).length;

    setAnalytics({
      totalProducts,
      activeAlerts,
      averageSavings,
      priceDropsDetected,
    });
  };

  const getProductAnalytics = () => {
    return analyticsData.map(item => {
      const currentPrice = parseFloat(item.currentPrice || 0);
      const targetPrice = parseFloat(item.targetPrice || currentPrice);
      const lowestPrice = parseFloat(item.lowestPrice || currentPrice);
      const highestPrice = parseFloat(item.highestPrice || currentPrice);
      const avgPrice = parseFloat(item.averagePrice || currentPrice);

      // Calculate trend if not provided
      let trend = item.trend || 'stable';
      let trendPercent = Math.abs(parseFloat(item.priceChangePercentage || 0));

      if (!item.trend) {
        if (currentPrice < targetPrice) {
          trend = 'down';
          trendPercent = Math.abs(((targetPrice - currentPrice) / targetPrice) * 100);
        } else if (currentPrice > targetPrice) {
          trend = 'up';
          trendPercent = Math.abs(((currentPrice - targetPrice) / targetPrice) * 100);
        }
      }

      return {
        id: item.productId,
        product: item.productTitle || 'Unknown Product',
        platform: item.platform || 'Unknown',
        currentPrice,
        targetPrice,
        lowestPrice,
        highestPrice,
        avgPrice: Math.round(avgPrice),
        trend,
        trendPercent: parseFloat(trendPercent.toFixed(2)),
        alertEnabled: item.alertEnabled,
        lastTracked: item.lastTracked,
        priceHistory: item.priceHistory || [],
      };
    });
  };

  const handleManualTrack = async () => {
    try {
      showSnackbar('Triggering manual price tracking...', 'info');
      await axios.post('http://localhost:8080/api/analytics/track-prices');
      
      // Wait a moment then refresh data
      setTimeout(() => {
        fetchAnalyticsData();
      }, 2000);
    } catch (error) {
      console.error('Error triggering manual tracking:', error);
      showSnackbar('Failed to trigger price tracking', 'error');
    }
  };

  const showSnackbar = (message, severity = 'success') => {
    setSnackbar({ open: true, message, severity });
  };

  const handleCloseSnackbar = () => {
    setSnackbar(prev => ({ ...prev, open: false }));
  };

  const insights = [
    {
      title: 'Products Tracked',
      description: 'Items with alerts enabled being monitored',
      icon: <Analytics sx={{ color: '#6366f1' }} />,
      value: analytics.totalProducts.toString(),
      color: '#6366f1',
    },
    {
      title: 'Active Alerts',
      description: 'Price drop alerts currently enabled',
      icon: <Timeline sx={{ color: '#10b981' }} />,
      value: analytics.activeAlerts.toString(),
      color: '#10b981',
    },
    {
      title: 'Potential Savings',
      description: 'Average savings per product if target met',
      icon: <PriceChange sx={{ color: '#f59e0b' }} />,
      value: `₹${analytics.averageSavings.toLocaleString()}`,
      color: '#f59e0b',
    },
    {
      title: 'Price Goals Met',
      description: 'Products at or below your target price',
      icon: <TrendingDown sx={{ color: '#ef4444' }} />,
      value: analytics.priceDropsDetected.toString(),
      color: '#ef4444',
    },
  ];

  if (isLoading) {
    return (
      <Box sx={{ p: 4, display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '60vh' }}>
        <Box sx={{ textAlign: 'center' }}>
          <CircularProgress size={60} />
          <Typography variant="h6" sx={{ mt: 3, color: 'text.secondary' }}>
            Loading analytics data...
          </Typography>
        </Box>
      </Box>
    );
  }

  if (!user) {
    return (
      <Box sx={{ p: 4, textAlign: 'center' }}>
        <Analytics sx={{ fontSize: 100, color: 'text.secondary', mb: 2 }} />
        <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 2 }}>
          Please Login
        </Typography>
        <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
          You need to be logged in to view price analytics
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

  const productAnalytics = getProductAnalytics();

  return (
    <Box sx={{ p: 4 }}>
      {/* Header */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Box>
          <Typography variant="h3" sx={{ fontWeight: 'bold', mb: 1 }}>
            Price Analytics
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Track and analyze price trends for products with alerts enabled
          </Typography>
        </Box>
        <Box sx={{ display: 'flex', gap: 2 }}>
          <Tooltip title="Manually trigger price tracking">
            <Button
              variant="outlined"
              startIcon={<ShowChart />}
              onClick={handleManualTrack}
              sx={{ borderColor: '#10b981', color: '#10b981' }}
            >
              Track Now
            </Button>
          </Tooltip>
          <Button
            variant="outlined"
            startIcon={<Refresh />}
            onClick={fetchAnalyticsData}
            sx={{ borderColor: '#6366f1', color: '#6366f1' }}
          >
            Refresh
          </Button>
        </Box>
      </Box>

      {/* Insights Cards */}
      <Grid container spacing={3} sx={{ mb: 6 }}>
        {insights.map((insight, index) => (
          <Grid item xs={12} sm={6} md={3} key={index}>
            <Card
              sx={{
                borderRadius: 2,
                boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
                transition: 'transform 0.2s',
                '&:hover': {
                  transform: 'translateY(-2px)',
                  boxShadow: '0 4px 12px rgba(0,0,0,0.15)',
                },
              }}
            >
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                  <Avatar
                    sx={{
                      backgroundColor: `${insight.color}15`,
                      mr: 2,
                      width: 56,
                      height: 56,
                    }}
                  >
                    {insight.icon}
                  </Avatar>
                  <Typography variant="h4" sx={{ fontWeight: 'bold', color: insight.color }}>
                    {insight.value}
                  </Typography>
                </Box>
                <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 1 }}>
                  {insight.title}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {insight.description}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      {/* Price Tracking Table */}
      {productAnalytics.length > 0 ? (
        <Paper elevation={1} sx={{ borderRadius: 2, overflow: 'hidden', mb: 6 }}>
          <Box sx={{ p: 3, backgroundColor: '#f8fafc', borderBottom: '1px solid #e2e8f0' }}>
            <Typography variant="h5" sx={{ fontWeight: 'bold' }}>
              Tracked Products Price Analysis
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
              Real-time price tracking for {productAnalytics.length} product{productAnalytics.length !== 1 ? 's' : ''} with alerts enabled
            </Typography>
          </Box>

          {productAnalytics.map((item, index) => (
            <Box
              key={item.id || index}
              sx={{
                p: 3,
                borderBottom: index < productAnalytics.length - 1 ? '1px solid #e2e8f0' : 'none',
                '&:hover': {
                  backgroundColor: '#f9fafb',
                },
              }}
            >
              <Grid container spacing={3} alignItems="center">
                <Grid item xs={12} md={3}>
                  <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 1 }}>
                    {item.product}
                  </Typography>
                  <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap', mb: 1 }}>
                    <Chip
                      icon={item.trend === 'down' ? <TrendingDown /> : item.trend === 'up' ? <TrendingUp /> : <ShowChart />}
                      label={`${item.trendPercent}% ${item.trend}`}
                      color={item.trend === 'down' ? 'success' : item.trend === 'up' ? 'error' : 'default'}
                      size="small"
                    />
                    {item.alertEnabled && (
                      <Chip
                        icon={<CheckCircle />}
                        label="Alert ON"
                        color="primary"
                        size="small"
                      />
                    )}
                    {item.currentPrice <= item.targetPrice && (
                      <Chip
                        icon={<CheckCircle />}
                        label="Target Met!"
                        color="success"
                        size="small"
                      />
                    )}
                  </Box>
                  <Typography variant="caption" color="text.secondary" sx={{ display: 'block' }}>
                    Platform: {item.platform}
                  </Typography>
                  {item.lastTracked && (
                    <Typography variant="caption" color="text.secondary" sx={{ display: 'block' }}>
                      Last tracked: {new Date(item.lastTracked).toLocaleDateString()}
                    </Typography>
                  )}
                </Grid>

                <Grid item xs={6} md={2}>
                  <Typography variant="body2" color="text.secondary">
                    Current Price
                  </Typography>
                  <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#6366f1' }}>
                    ₹{item.currentPrice.toLocaleString()}
                  </Typography>
                </Grid>

                <Grid item xs={6} md={2}>
                  <Typography variant="body2" color="text.secondary">
                    Target Price
                  </Typography>
                  <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#10b981' }}>
                    ₹{item.targetPrice.toLocaleString()}
                  </Typography>
                </Grid>

                <Grid item xs={6} md={2}>
                  <Typography variant="body2" color="text.secondary">
                    Lowest Tracked
                  </Typography>
                  <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#22c55e' }}>
                    ₹{Math.round(item.lowestPrice).toLocaleString()}
                  </Typography>
                </Grid>

                <Grid item xs={6} md={2}>
                  <Typography variant="body2" color="text.secondary">
                    Average Price
                  </Typography>
                  <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                    ₹{item.avgPrice.toLocaleString()}
                  </Typography>
                </Grid>

                <Grid item xs={12} md={1}>
                  <Tooltip title={`Price history: ${item.priceHistory.length} records`}>
                    <Box sx={{ textAlign: 'center' }}>
                      <ShowChart sx={{ fontSize: 32, color: '#6366f1', mb: 1 }} />
                      <Typography variant="caption" color="text.secondary">
                        {item.priceHistory.length} pts
                      </Typography>
                    </Box>
                  </Tooltip>
                </Grid>
              </Grid>
            </Box>
          ))}
        </Paper>
      ) : (
        <Paper elevation={1} sx={{ borderRadius: 2, p: 6, textAlign: 'center', mb: 6 }}>
          <ErrorOutline sx={{ fontSize: 80, color: 'text.secondary', mb: 2 }} />
          <Typography variant="h5" sx={{ fontWeight: 'bold', mb: 2 }}>
            No Products Being Tracked
          </Typography>
          <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
            Enable alerts on your wishlist items to start tracking their prices
          </Typography>
          <Button
            variant="contained"
            size="large"
            onClick={() => navigate('/wishlist')}
            sx={{
              backgroundColor: '#6366f1',
              '&:hover': { backgroundColor: '#5b21b6' },
            }}
          >
            Go to Wishlist
          </Button>
        </Paper>
      )}

      {/* Market Insights */}
      <Box sx={{ mt: 6 }}>
        <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 3 }}>
          Market Insights
        </Typography>
        <Grid container spacing={3}>
          <Grid item xs={12} md={6}>
            <Card sx={{ p: 3, borderRadius: 2, height: '100%' }}>
              <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2 }}>
                Price Trend Tips
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                Based on market patterns and shopping trends:
              </Typography>
              <Box sx={{ pl: 2 }}>
                <Typography variant="body2" sx={{ mb: 1 }}>
                  • Electronics prices drop by 10-15% during major sales events
                </Typography>
                <Typography variant="body2" sx={{ mb: 1 }}>
                  • Smartphone prices typically decrease 3-4 months after launch
                </Typography>
                <Typography variant="body2" sx={{ mb: 1 }}>
                  • Audio accessories see maximum discounts in Q4
                </Typography>
                <Typography variant="body2">
                  • Laptops are cheapest during back-to-school seasons (July-August)
                </Typography>
              </Box>
            </Card>
          </Grid>

          <Grid item xs={12} md={6}>
            <Card sx={{ p: 3, borderRadius: 2, height: '100%' }}>
              <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2 }}>
                Savings Strategies
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                Maximize your savings with SmartShopper:
              </Typography>
              <Box sx={{ pl: 2 }}>
                <Typography variant="body2" sx={{ mb: 1 }}>
                  • Set price alerts for all items in your wishlist
                </Typography>
                <Typography variant="body2" sx={{ mb: 1 }}>
                  • Compare prices across multiple platforms before buying
                </Typography>
                <Typography variant="body2" sx={{ mb: 1 }}>
                  • Wait for festival sales (Diwali, Amazon Prime Day, etc.)
                </Typography>
                <Typography variant="body2">
                  • Use our price analytics to identify the best time to buy
                </Typography>
              </Box>
            </Card>
          </Grid>
        </Grid>
      </Box>

      {/* Info Banner */}
      {productAnalytics.length > 0 && (
        <Card
          sx={{
            mt: 4,
            p: 3,
            backgroundColor: '#eff6ff',
            border: '1px solid #bfdbfe',
            borderRadius: 2,
          }}
        >
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
            <Timeline sx={{ fontSize: 40, color: '#3b82f6' }} />
            <Box>
              <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 1 }}>
                Automatic Price Tracking
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Prices are automatically tracked every 6 hours for products with alerts enabled. You'll receive email
                notifications when prices drop below your target price.
              </Typography>
            </Box>
          </Box>
        </Card>
      )}

      {/* Snackbar */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={4000}
        onClose={handleCloseSnackbar}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert onClose={handleCloseSnackbar} severity={snackbar.severity} sx={{ width: '100%' }}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default PriceAnalytics;