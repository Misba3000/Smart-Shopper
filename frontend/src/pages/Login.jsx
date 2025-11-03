import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import {
  Box,
  Card,
  CardContent,
  TextField,
  Button,
  Typography,
  Alert,
  Avatar,
  Container,
  Collapse,
} from '@mui/material';
import { LockOutlined, AdminPanelSettings } from '@mui/icons-material';
import { useAuth } from '../context/AuthContext';

const Login = () => {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    verificationCode: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [showOtpField, setShowOtpField] = useState(true);

  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const from = location.state?.from?.pathname || '/';

  // Check if email is admin
  const isAdminEmail = (email) => {
    const adminEmails = ['admin@smartprice.com', 'admin@smartshopper.com'];
    return adminEmails.includes(email.toLowerCase().trim());
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    
    setFormData({
      ...formData,
      [name]: value,
    });
    
    // Hide OTP field for admin emails
    if (name === 'email') {
      setShowOtpField(!isAdminEmail(value));
    }
    
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Validation
    if (!formData.email || !formData.password) {
      setError('Please fill in email and password');
      return;
    }

    // Only require OTP for non-admin users
    if (showOtpField && !formData.verificationCode) {
      setError('Please enter the verification code');
      return;
    }

    setLoading(true);
    setError('');

    try {
      // Pass OTP or empty string for admin
      const otpCode = showOtpField ? formData.verificationCode : '';
      await login(formData.email, formData.password, otpCode);
      
      // Get user response to check role
      const loginResponse = await login(formData.email, formData.password, otpCode);
      
      // Navigate based on actual user role from response
      if (loginResponse && loginResponse.role === 'ADMIN') {
        navigate('/admin/dashboard');
      } else {
        navigate(from === '/login' ? '/' : from);
      }
    } catch (err) {
      const errorMessage = err.message || 'Failed to sign in. Please check your credentials.';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box
      sx={{
        minHeight: '100vh',
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        display: 'flex',
        alignItems: 'center',
      }}
    >
      <Container maxWidth="sm">
        <Card
          sx={{
            borderRadius: 3,
            boxShadow: '0 8px 32px rgba(0,0,0,0.1)',
          }}
        >
          <CardContent sx={{ p: 4 }}>
            <Box sx={{ textAlign: 'center', mb: 4 }}>
              <Avatar
                sx={{
                  bgcolor: isAdminEmail(formData.email) ? '#f59e0b' : '#6366f1',
                  width: 56,
                  height: 56,
                  mx: 'auto',
                  mb: 2,
                }}
              >
                {isAdminEmail(formData.email) ? <AdminPanelSettings /> : <LockOutlined />}
              </Avatar>
              <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 1 }}>
                {isAdminEmail(formData.email) ? 'Admin Login' : 'Welcome Back'}
              </Typography>
              <Typography variant="body1" color="text.secondary">
                {isAdminEmail(formData.email) 
                  ? 'Sign in to admin dashboard' 
                  : 'Sign in to your Smart Shopper account'}
              </Typography>
            </Box>

            {error && (
              <Alert severity="error" sx={{ mb: 3 }}>
                {error}
              </Alert>
            )}

            {location.state?.message && (
              <Alert severity="info" sx={{ mb: 3 }}>
                {location.state.message}
              </Alert>
            )}

            <form onSubmit={handleSubmit}>
              <TextField
                fullWidth
                label="Email Address"
                name="email"
                type="email"
                value={formData.email}
                onChange={handleChange}
                margin="normal"
                variant="outlined"
                required
                autoComplete="email"
              />
              
              <TextField
                fullWidth
                label="Password"
                name="password"
                type="password"
                value={formData.password}
                onChange={handleChange}
                margin="normal"
                variant="outlined"
                required
                autoComplete="current-password"
              />

              {/* Conditionally show OTP field */}
              <Collapse in={showOtpField}>
                <TextField
                  fullWidth
                  label="Verification Code"
                  name="verificationCode"
                  type="text"
                  placeholder="Enter 6745"
                  value={formData.verificationCode}
                  onChange={handleChange}
                  margin="normal"
                  variant="outlined"
                  required={showOtpField}
                  helperText="Check your email for the verification code"
                />
              </Collapse>

              {!showOtpField && (
                <Alert severity="info" sx={{ mt: 2 }}>
                  Admin login - No verification code required
                </Alert>
              )}

              <Button
                type="submit"
                fullWidth
                variant="contained"
                disabled={loading}
                sx={{
                  mt: 3,
                  mb: 2,
                  py: 1.5,
                  backgroundColor: isAdminEmail(formData.email) ? '#f59e0b' : '#6366f1',
                  '&:hover': {
                    backgroundColor: isAdminEmail(formData.email) ? '#d97706' : '#5b21b6',
                  },
                }}
              >
                {loading ? 'Signing In...' : 'Sign In'}
              </Button>

              <Box sx={{ textAlign: 'center' }}>
                <Typography variant="body2">
                  Don't have an account?{' '}
                  <Link
                    to="/register"
                    style={{
                      color: '#6366f1',
                      textDecoration: 'none',
                      fontWeight: 'bold',
                    }}
                  >
                    Sign up here
                  </Link>
                </Typography>
              </Box>
            </form>
          </CardContent>
        </Card>
      </Container>
    </Box>
  );
};

export default Login;