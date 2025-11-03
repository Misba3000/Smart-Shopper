import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Box, CircularProgress, Typography, Paper } from '@mui/material';
import { Block } from '@mui/icons-material';

const AdminRoute = ({ children }) => {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <Box
        sx={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          minHeight: '100vh',
        }}
      >
        <CircularProgress />
      </Box>
    );
  }

  // Not logged in - redirect to login
  if (!user) {
    return <Navigate to="/login" state={{ message: 'Please login to access admin dashboard' }} />;
  }

  // Logged in but not admin - show access denied
  if (user.role !== 'ADMIN') {
    return (
      <Box
        sx={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          minHeight: '80vh',
          p: 4,
        }}
      >
        <Paper
          elevation={3}
          sx={{
            p: 6,
            textAlign: 'center',
            maxWidth: 500,
            borderRadius: 2,
          }}
        >
          <Block sx={{ fontSize: 80, color: 'error.main', mb: 2 }} />
          <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 2, color: 'error.main' }}>
            Access Denied
          </Typography>
          <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
            You don't have permission to access the admin dashboard. This area is restricted to administrators only.
          </Typography>
          <Typography variant="body2" color="text.secondary">
            If you believe this is an error, please contact support.
          </Typography>
        </Paper>
      </Box>
    );
  }

  // Admin user - allow access
  return children;
};

export default AdminRoute;