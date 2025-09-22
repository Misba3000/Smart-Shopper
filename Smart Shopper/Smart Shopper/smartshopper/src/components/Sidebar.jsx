import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import {
  Box,
  Drawer,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Typography,
  Avatar,
  Divider,
  Button,
} from '@mui/material';
import {
  Search,
  FavoriteOutlined,
  TrendingUp,
  Dashboard,
  ShoppingCart,
  Person,
  Logout,
  Store,
} from '@mui/icons-material';
import { useAuth } from '../context/AuthContext';

const drawerWidth = 280;

const menuItems = [
  { text: 'Search & Compare', icon: <Search />, path: '/' },
  { text: 'My Wishlist', icon: <FavoriteOutlined />, path: '/wishlist', protected: true },
  { text: 'Price Analytics', icon: <TrendingUp />, path: '/price-analytics' },
  { text: 'Admin Dashboard', icon: <Dashboard />, path: '/admin' },
];

const quickActions = [
  { text: 'Products', icon: <Store />, path: '/products' },
  { text: 'Cart', icon: <ShoppingCart />, path: '/cart', protected: true },
];

const Sidebar = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  const handleNavigation = (path, isProtected = false) => {
    if (isProtected && !user) {
      navigate('/login');
      return;
    }
    navigate(path);
  };

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <Drawer
      variant="permanent"
      sx={{
        width: drawerWidth,
        flexShrink: 0,
        '& .MuiDrawer-paper': {
          width: drawerWidth,
          boxSink: 'border-box',
          backgroundColor: '#f8fafc', // Changed to light background
          color: '#334155', // Darker text color for better contrast
          borderRight: '1px solid #e2e8f0', // Subtle border
        },
      }}
    >
      <Box sx={{ p: 3 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
          <Avatar
            sx={{
              bgcolor: '#6366f1', // Keeping the purple accent
              mr: 2,
              width: 40,
              height: 40,
              color: 'white',
            }}
          >
            PC
          </Avatar>
          <Box>
            <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#1e293b' }}>
              PriceCompare
            </Typography>
            <Typography variant="body2" sx={{ color: '#64748b' }}>
              Smart Price Tracking
            </Typography>
          </Box>
        </Box>

        <Typography
          variant="overline"
          sx={{ color: '#64748b', fontWeight: 'bold', mb: 1, display: 'block' }}
        >
          MAIN MENU
        </Typography>

        <List sx={{ mb: 2 }}>
          {menuItems.map((item) => (
            <ListItem key={item.text} disablePadding>
              <ListItemButton
                onClick={() => handleNavigation(item.path, item.protected)}
                sx={{
                  borderRadius: 1,
                  mb: 0.5,
                  backgroundColor:
                    location.pathname === item.path ? '#e0e7ff' : 'transparent',
                  color: location.pathname === item.path ? '#4338ca' : '#475569',
                  '&:hover': {
                    backgroundColor: '#e0e7ff',
                    color: '#4338ca',
                  },
                }}
              >
                <ListItemIcon sx={{ 
                  color: 'inherit', 
                  minWidth: 36,
                  '&.MuiListItemIcon-root': {
                    color: 'inherit'
                  }
                }}>
                  {item.icon}
                </ListItemIcon>
                <ListItemText 
                  primary={item.text} 
                  sx={{
                    '& .MuiTypography-root': {
                      fontWeight: location.pathname === item.path ? '600' : 'normal'
                    }
                  }}
                />
              </ListItemButton>
            </ListItem>
          ))}
        </List>

        <Typography
          variant="overline"
          sx={{ color: '#64748b', fontWeight: 'bold', mb: 1, display: 'block' }}
        >
          QUICK ACTIONS
        </Typography>

        <List sx={{ mb: 2 }}>
          {quickActions.map((item) => (
            <ListItem key={item.text} disablePadding>
              <ListItemButton
                onClick={() => handleNavigation(item.path, item.protected)}
                sx={{
                  borderRadius: 1,
                  mb: 0.5,
                  backgroundColor:
                    location.pathname === item.path ? '#e0e7ff' : 'transparent',
                  color: location.pathname === item.path ? '#4338ca' : '#475569',
                  '&:hover': {
                    backgroundColor: '#e0e7ff',
                    color: '#4338ca',
                  },
                }}
              >
                <ListItemIcon sx={{ 
                  color: 'inherit', 
                  minWidth: 36,
                  '&.MuiListItemIcon-root': {
                    color: 'inherit'
                  }
                }}>
                  {item.icon}
                </ListItemIcon>
                <ListItemText 
                  primary={item.text} 
                  sx={{
                    '& .MuiTypography-root': {
                      fontWeight: location.pathname === item.path ? '600' : 'normal'
                    }
                  }}
                />
              </ListItemButton>
            </ListItem>
          ))}
        </List>

        {user && (
          <Box
            sx={{
              mt: 'auto',
              p: 2,
              backgroundColor: '#d1fae5',
              borderRadius: 2,
              mb: 2,
              border: '1px solid #a7f3d0',
            }}
          >
            <Typography variant="body2" sx={{ fontWeight: 'bold', color: '#065f46' }}>
              Price Alerts Active
            </Typography>
            <Typography variant="caption" sx={{ color: '#047857' }}>
              3 products being monitored
            </Typography>
          </Box>
        )}
      </Box>

      <Box sx={{ mt: 'auto', p: 2 }}>
        <Divider sx={{ mb: 2, backgroundColor: '#e2e8f0' }} />
        {user ? (
          <Box>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
              <Avatar sx={{ 
                width: 32, 
                height: 32, 
                mr: 2, 
                fontSize: 14,
                bgcolor: '#6366f1',
                color: 'white'
              }}>
                {user.name?.charAt(0).toUpperCase()}
              </Avatar>
              <Box>
                <Typography variant="body2" sx={{ fontWeight: 'bold', color: '#1e293b' }}>
                  {user.name}
                </Typography>
                <Typography variant="caption" sx={{ color: '#64748b' }}>
                  Save money with smart tracking
                </Typography>
              </Box>
            </Box>
            <Button
              fullWidth
              startIcon={<Logout />}
              onClick={handleLogout}
              sx={{
                color: '#dc2626',
                '&:hover': {
                  backgroundColor: '#fee2e2',
                },
              }}
            >
              Logout
            </Button>
          </Box>
        ) : (
          <Box>
            <Button
              fullWidth
              startIcon={<Person />}
              onClick={() => navigate('/login')}
              sx={{
                color: 'white',
                backgroundColor: '#6366f1',
                mb: 1,
                '&:hover': {
                  backgroundColor: '#4f46e5',
                },
              }}
            >
              Login
            </Button>
            <Button
              fullWidth
              variant="outlined"
              onClick={() => navigate('/register')}
              sx={{
                color: '#475569',
                borderColor: '#cbd5e1',
                '&:hover': {
                  backgroundColor: '#f1f5f9',
                  borderColor: '#94a3b8',
                },
              }}
            >
              Sign Up
            </Button>
          </Box>
        )}
      </Box>
    </Drawer>
  );
};

export default Sidebar;