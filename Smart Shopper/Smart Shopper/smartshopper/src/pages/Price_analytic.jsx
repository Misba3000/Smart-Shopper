import React from 'react';
import {
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  Paper,
  Avatar,
  Chip,
} from '@mui/material';
import {
  TrendingUp,
  TrendingDown,
  ShowChart,
  Timeline,
  PriceChange,
  Analytics,
} from '@mui/icons-material';

const mockAnalytics = [
  {
    product: 'iPhone 15 Pro',
    currentPrice: 129900,
    lowestPrice: 125000,
    highestPrice: 139900,
    avgPrice: 132000,
    priceDrops: 8,
    trend: 'down',
    trendPercent: 7.2,
  },
  {
    product: 'Samsung Galaxy S24',
    currentPrice: 89999,
    lowestPrice: 84999,
    highestPrice: 94999,
    avgPrice: 90500,
    priceDrops: 12,
    trend: 'down',
    trendPercent: 5.3,
  },
  {
    product: 'Sony WH-1000XM5',
    currentPrice: 29999,
    lowestPrice: 27999,
    highestPrice: 34999,
    avgPrice: 31200,
    priceDrops: 6,
    trend: 'up',
    trendPercent: 3.8,
  },
];

const insights = [
  {
    title: 'Best Time to Buy',
    description: 'Electronics prices typically drop during festival seasons',
    icon: <Timeline sx={{ color: '#10b981' }} />,
    value: 'Oct - Nov',
  },
  {
    title: 'Average Savings',
    description: 'Users save on average when using price tracking',
    icon: <PriceChange sx={{ color: '#f59e0b' }} />,
    value: '₹8,500',
  },
  {
    title: 'Price Drops Tracked',
    description: 'Total price drops detected this month',
    icon: <TrendingDown sx={{ color: '#ef4444' }} />,
    value: '234',
  },
  {
    title: 'Products Monitored',
    description: 'Total products being tracked across platforms',
    icon: <Analytics sx={{ color: '#6366f1' }} />,
    value: '1,245',
  },
];

const PriceAnalytics = () => {
  return (
    <Box sx={{ p: 4 }}>
      <Typography variant="h3" sx={{ fontWeight: 'bold', mb: 1 }}>
        Price Analytics
      </Typography>
      <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
        Comprehensive insights into price trends and market analysis
      </Typography>

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
                },
              }}
            >
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                  <Avatar
                    sx={{
                      backgroundColor: 'rgba(99, 102, 241, 0.1)',
                      mr: 2,
                    }}
                  >
                    {insight.icon}
                  </Avatar>
                  <Typography variant="h4" sx={{ fontWeight: 'bold' }}>
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
      <Paper elevation={1} sx={{ borderRadius: 2, overflow: 'hidden' }}>
        <Box sx={{ p: 3, backgroundColor: '#f8fafc', borderBottom: '1px solid #e2e8f0' }}>
          <Typography variant="h5" sx={{ fontWeight: 'bold' }}>
            Product Price Analysis
          </Typography>
        </Box>
        
        {mockAnalytics.map((item, index) => (
          <Box
            key={index}
            sx={{
              p: 3,
              borderBottom: index < mockAnalytics.length - 1 ? '1px solid #e2e8f0' : 'none',
              '&:hover': {
                backgroundColor: '#f9fafb',
              },
            }}
          >
            <Grid container spacing={3} alignItems="center">
              <Grid item xs={12} md={3}>
                <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                  {item.product}
                </Typography>
                <Chip
                  icon={item.trend === 'down' ? <TrendingDown /> : <TrendingUp />}
                  label={`${item.trendPercent}% ${item.trend}`}
                  color={item.trend === 'down' ? 'success' : 'error'}
                  size="small"
                  sx={{ mt: 1 }}
                />
              </Grid>
              
              <Grid item xs={12} md={2}>
                <Typography variant="body2" color="text.secondary">
                  Current Price
                </Typography>
                <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#6366f1' }}>
                  ₹{item.currentPrice.toLocaleString()}
                </Typography>
              </Grid>
              
              <Grid item xs={12} md={2}>
                <Typography variant="body2" color="text.secondary">
                  Lowest Price
                </Typography>
                <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#10b981' }}>
                  ₹{item.lowestPrice.toLocaleString()}
                </Typography>
              </Grid>
              
              <Grid item xs={12} md={2}>
                <Typography variant="body2" color="text.secondary">
                  Highest Price
                </Typography>
                <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#ef4444' }}>
                  ₹{item.highestPrice.toLocaleString()}
                </Typography>
              </Grid>
              
              <Grid item xs={12} md={2}>
                <Typography variant="body2" color="text.secondary">
                  Average Price
                </Typography>
                <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                  ₹{item.avgPrice.toLocaleString()}
                </Typography>
              </Grid>
              
              <Grid item xs={12} md={1}>
                <Box sx={{ textAlign: 'center' }}>
                  <ShowChart sx={{ fontSize: 32, color: '#6366f1', mb: 1 }} />
                  <Typography variant="caption" color="text.secondary">
                    {item.priceDrops} drops
                  </Typography>
                </Box>
              </Grid>
            </Grid>
          </Box>
        ))}
      </Paper>

      {/* Market Insights */}
      <Box sx={{ mt: 6 }}>
        <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 3 }}>
          Market Insights
        </Typography>
        <Grid container spacing={3}>
          <Grid item xs={12} md={6}>
            <Card sx={{ p: 3, borderRadius: 2, height: '100%' }}>
              <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2 }}>
                Price Trend Analysis
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                Based on historical data and market patterns:
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
                  • Laptops are cheapest during back-to-school seasons
                </Typography>
              </Box>
            </Card>
          </Grid>
          
          <Grid item xs={12} md={6}>
            <Card sx={{ p: 3, borderRadius: 2, height: '100%' }}>
              <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2 }}>
                Savings Opportunities
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                Current market opportunities for maximum savings:
              </Typography>
              <Box sx={{ pl: 2 }}>
                <Typography variant="body2" sx={{ mb: 1 }}>
                  • Set price alerts for items in your wishlist
                </Typography>
                <Typography variant="body2" sx={{ mb: 1 }}>
                  • Compare prices across 5+ platforms before buying
                </Typography>
                <Typography variant="body2" sx={{ mb: 1 }}>
                  • Monitor price history for 30 days minimum
                </Typography>
                <Typography variant="body2">
                  • Consider refurbished options for 20-30% savings
                </Typography>
              </Box>
            </Card>
          </Grid>
        </Grid>
      </Box>
    </Box>
  );
};

export default PriceAnalytics;