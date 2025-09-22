import React from 'react';
import {
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  Avatar,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  LinearProgress,
} from '@mui/material';
import {
  People,
  ShoppingBag,
  TrendingUp,
  Notifications,
  Store,
  Analytics,
} from '@mui/icons-material';

const statsData = [
  {
    title: 'Total Users',
    value: '12,847',
    change: '+12.5%',
    icon: <People sx={{ color: '#3b82f6' }} />,
    color: '#3b82f6',
    bgColor: '#dbeafe',
  },
  {
    title: 'Products Tracked',
    value: '45,231',
    change: '+8.2%',
    icon: <Store sx={{ color: '#10b981' }} />,
    color: '#10b981',
    bgColor: '#d1fae5',
  },
  {
    title: 'Price Alerts Sent',
    value: '8,945',
    change: '+23.1%',
    icon: <Notifications sx={{ color: '#f59e0b' }} />,
    color: '#f59e0b',
    bgColor: '#fef3c7',
  },
  {
    title: 'Revenue',
    value: '₹2,45,678',
    change: '+15.3%',
    icon: <TrendingUp sx={{ color: '#ec4899' }} />,
    color: '#ec4899',
    bgColor: '#fce7f3',
  },
];

const recentUsers = [
  { id: 1, name: 'John Doe', email: 'john@example.com', status: 'Active', joined: '2024-01-15' },
  { id: 2, name: 'Jane Smith', email: 'jane@example.com', status: 'Active', joined: '2024-01-14' },
  { id: 3, name: 'Mike Johnson', email: 'mike@example.com', status: 'Inactive', joined: '2024-01-13' },
  { id: 4, name: 'Sarah Wilson', email: 'sarah@example.com', status: 'Active', joined: '2024-01-12' },
  { id: 5, name: 'Tom Brown', email: 'tom@example.com', status: 'Pending', joined: '2024-01-11' },
];

const topProducts = [
  { name: 'iPhone 15 Pro', searches: 2456, alerts: 892, trend: 'up' },
  { name: 'Samsung Galaxy S24', searches: 1987, alerts: 567, trend: 'up' },
  { name: 'MacBook Air M3', searches: 1543, alerts: 423, trend: 'down' },
  { name: 'Sony WH-1000XM5', searches: 1234, alerts: 345, trend: 'up' },
  { name: 'iPad Pro', searches: 987, alerts: 234, trend: 'down' },
];

const AdminDashboard = () => {
  return (
    <Box sx={{ p: 4 }}>
      <Typography variant="h3" sx={{ fontWeight: 'bold', mb: 1 }}>
        Admin Dashboard
      </Typography>
      <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
        Monitor platform performance and user analytics
      </Typography>

      {/* Stats Cards */}
      <Grid container spacing={3} sx={{ mb: 6 }}>
        {statsData.map((stat, index) => (
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
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 2 }}>
                  <Avatar
                    sx={{
                      backgroundColor: stat.bgColor,
                      width: 48,
                      height: 48,
                    }}
                  >
                    {stat.icon}
                  </Avatar>
                  <Chip
                    label={stat.change}
                    color="success"
                    size="small"
                    sx={{ fontWeight: 'bold' }}
                  />
                </Box>
                <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 1 }}>
                  {stat.value}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {stat.title}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Grid container spacing={4}>
        {/* Recent Users */}
        <Grid item xs={12} lg={8}>
          <Card sx={{ borderRadius: 2 }}>
            <Box sx={{ p: 3, borderBottom: '1px solid #e2e8f0' }}>
              <Typography variant="h5" sx={{ fontWeight: 'bold' }}>
                Recent Users
              </Typography>
            </Box>
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Name</TableCell>
                    <TableCell>Email</TableCell>
                    <TableCell>Status</TableCell>
                    <TableCell>Joined</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {recentUsers.map((user) => (
                    <TableRow key={user.id} hover>
                      <TableCell sx={{ fontWeight: 'medium' }}>{user.name}</TableCell>
                      <TableCell>{user.email}</TableCell>
                      <TableCell>
                        <Chip
                          label={user.status}
                          color={
                            user.status === 'Active' ? 'success' :
                            user.status === 'Inactive' ? 'error' : 'warning'
                          }
                          size="small"
                        />
                      </TableCell>
                      <TableCell>{user.joined}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </Card>
        </Grid>

        {/* Top Products */}
        <Grid item xs={12} lg={4}>
          <Card sx={{ borderRadius: 2, height: 'fit-content' }}>
            <Box sx={{ p: 3, borderBottom: '1px solid #e2e8f0' }}>
              <Typography variant="h5" sx={{ fontWeight: 'bold' }}>
                Top Products
              </Typography>
            </Box>
            <Box sx={{ p: 2 }}>
              {topProducts.map((product, index) => (
                <Box
                  key={index}
                  sx={{
                    p: 2,
                    borderRadius: 1,
                    mb: 1,
                    '&:hover': {
                      backgroundColor: '#f9fafb',
                    },
                  }}
                >
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
                    <Typography variant="body1" sx={{ fontWeight: 'medium' }}>
                      {product.name}
                    </Typography>
                    <Chip
                      label={product.trend}
                      color={product.trend === 'up' ? 'success' : 'error'}
                      size="small"
                    />
                  </Box>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                    {product.searches} searches • {product.alerts} alerts
                  </Typography>
                  <LinearProgress
                    variant="determinate"
                    value={(product.searches / 2500) * 100}
                    sx={{
                      height: 4,
                      borderRadius: 2,
                      backgroundColor: '#e2e8f0',
                      '& .MuiLinearProgress-bar': {
                        backgroundColor: '#6366f1',
                      },
                    }}
                  />
                </Box>
              ))}
            </Box>
          </Card>
        </Grid>
      </Grid>

      {/* System Health */}
      <Grid container spacing={4} sx={{ mt: 2 }}>
        <Grid item xs={12} md={6}>
          <Card sx={{ p: 3, borderRadius: 2 }}>
            <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 3 }}>
              System Performance
            </Typography>
            
            <Box sx={{ mb: 3 }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                <Typography variant="body2">API Response Time</Typography>
                <Typography variant="body2">125ms</Typography>
              </Box>
              <LinearProgress
                variant="determinate"
                value={85}
                sx={{
                  height: 6,
                  borderRadius: 3,
                  backgroundColor: '#e2e8f0',
                  '& .MuiLinearProgress-bar': {
                    backgroundColor: '#10b981',
                  },
                }}
              />
            </Box>

            <Box sx={{ mb: 3 }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                <Typography variant="body2">Database Load</Typography>
                <Typography variant="body2">67%</Typography>
              </Box>
              <LinearProgress
                variant="determinate"
                value={67}
                sx={{
                  height: 6,
                  borderRadius: 3,
                  backgroundColor: '#e2e8f0',
                  '& .MuiLinearProgress-bar': {
                    backgroundColor: '#f59e0b',
                  },
                }}
              />
            </Box>

            <Box>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                <Typography variant="body2">Server Uptime</Typography>
                <Typography variant="body2">99.9%</Typography>
              </Box>
              <LinearProgress
                variant="determinate"
                value={99}
                sx={{
                  height: 6,
                  borderRadius: 3,
                  backgroundColor: '#e2e8f0',
                  '& .MuiLinearProgress-bar': {
                    backgroundColor: '#10b981',
                  },
                }}
              />
            </Box>
          </Card>
        </Grid>

        <Grid item xs={12} md={6}>
          <Card sx={{ p: 3, borderRadius: 2 }}>
            <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 3 }}>
              Recent Activity
            </Typography>
            
            <Box sx={{ space: 2 }}>
              {[
                { action: 'New user registration', time: '2 minutes ago', type: 'success' },
                { action: 'Price alert triggered', time: '5 minutes ago', type: 'info' },
                { action: 'System backup completed', time: '1 hour ago', type: 'success' },
                { action: 'Database optimization', time: '3 hours ago', type: 'warning' },
                { action: 'Security scan completed', time: '6 hours ago', type: 'success' },
              ].map((activity, index) => (
                <Box
                  key={index}
                  sx={{
                    display: 'flex',
                    alignItems: 'center',
                    p: 2,
                    borderRadius: 1,
                    mb: 1,
                    backgroundColor: '#f9fafb',
                  }}
                >
                  <Box
                    sx={{
                      width: 8,
                      height: 8,
                      borderRadius: '50%',
                      backgroundColor:
                        activity.type === 'success' ? '#10b981' :
                        activity.type === 'warning' ? '#f59e0b' : '#6366f1',
                      mr: 2,
                    }}
                  />
                  <Box sx={{ flexGrow: 1 }}>
                    <Typography variant="body2" sx={{ fontWeight: 'medium' }}>
                      {activity.action}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      {activity.time}
                    </Typography>
                  </Box>
                </Box>
              ))}
            </Box>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default AdminDashboard;

// Future Scope
// import React, { useEffect, useState } from "react";
// import axios from "axios";
// import {
//   Box, Typography, Grid, Card, CardContent, Avatar, Chip,
//   Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, LinearProgress
// } from "@mui/material";
// import { People, Store, Notifications, TrendingUp } from "@mui/icons-material";

// const AdminDashboard = () => {
//   const [stats, setStats] = useState({});
//   const [recentUsers, setRecentUsers] = useState([]);
//   const [topProducts, setTopProducts] = useState([]);
//   const [systemPerf, setSystemPerf] = useState({});
//   const [activities, setActivities] = useState([]);

//   useEffect(() => {
//     axios.get("http://localhost:8080/api/admin/stats").then(res => setStats(res.data));
//     axios.get("http://localhost:8080/api/admin/recent-users").then(res => setRecentUsers(res.data));
//     axios.get("http://localhost:8080/api/admin/top-products").then(res => setTopProducts(res.data));
//     axios.get("http://localhost:8080/api/admin/system-performance").then(res => setSystemPerf(res.data));
//     axios.get("http://localhost:8080/api/admin/recent-activity").then(res => setActivities(res.data));
//   }, []);

//   const statsData = [
//     { title: "Total Users", value: stats.totalUsers, icon: <People />, color: "#3b82f6", bgColor: "#dbeafe" },
//     { title: "Products Tracked", value: stats.productsTracked, icon: <Store />, color: "#10b981", bgColor: "#d1fae5" },
//     { title: "Price Alerts Sent", value: stats.priceAlertsSent, icon: <Notifications />, color: "#f59e0b", bgColor: "#fef3c7" },
//     { title: "Revenue", value: `₹${stats.revenue}`, icon: <TrendingUp />, color: "#ec4899", bgColor: "#fce7f3" },
//   ];

//   return (
//     <Box sx={{ p: 4 }}>
//       <Typography variant="h3" sx={{ fontWeight: "bold", mb: 2 }}>
//         Admin Dashboard
//       </Typography>

//       {/* Stats */}
//       <Grid container spacing={3} sx={{ mb: 6 }}>
//         {statsData.map((stat, i) => (
//           <Grid item xs={12} sm={6} md={3} key={i}>
//             <Card sx={{ borderRadius: 2 }}>
//               <CardContent>
//                 <Avatar sx={{ backgroundColor: stat.bgColor }}>{stat.icon}</Avatar>
//                 <Typography variant="h4">{stat.value}</Typography>
//                 <Typography>{stat.title}</Typography>
//               </CardContent>
//             </Card>
//           </Grid>
//         ))}
//       </Grid>

//       {/* Recent Users */}
//       <Card>
//         <Typography variant="h5" sx={{ p: 2, fontWeight: "bold" }}>Recent Users</Typography>
//         <TableContainer>
//           <Table>
//             <TableHead>
//               <TableRow>
//                 <TableCell>Name</TableCell>
//                 <TableCell>Email</TableCell>
//                 <TableCell>Status</TableCell>
//                 <TableCell>Last Login</TableCell>
//               </TableRow>
//             </TableHead>
//             <TableBody>
//               {recentUsers.map(user => (
//                 <TableRow key={user.id}>
//                   <TableCell>{user.name}</TableCell>
//                   <TableCell>{user.email}</TableCell>
//                   <TableCell>
//                     <Chip label={user.status} color={user.status === "Active" ? "success" : "error"} />
//                   </TableCell>
//                   <TableCell>{user.lastLogin}</TableCell>
//                 </TableRow>
//               ))}
//             </TableBody>
//           </Table>
//         </TableContainer>
//       </Card>

//       {/* Top Products */}
//       <Card sx={{ mt: 4 }}>
//         <Typography variant="h5" sx={{ p: 2, fontWeight: "bold" }}>Top Products</Typography>
//         {topProducts.map((prod, i) => (
//           <Box key={i} sx={{ p: 2 }}>
//             <Typography>{prod.name}</Typography>
//             <Typography variant="body2">{prod.searches} searches • {prod.alerts} alerts</Typography>
//             <LinearProgress variant="determinate" value={(prod.searches / 2500) * 100} />
//           </Box>
//         ))}
//       </Card>

//       {/* System Performance */}
//       <Card sx={{ mt: 4, p: 2 }}>
//         <Typography variant="h5" sx={{ mb: 2 }}>System Performance</Typography>
//         <Typography>API Response Time: {systemPerf.apiResponseTime} ms</Typography>
//         <Typography>Database Load: {systemPerf.dbLoad}%</Typography>
//         <Typography>Uptime: {systemPerf.uptime}%</Typography>
//       </Card>

//       {/* Recent Activity */}
//       <Card sx={{ mt: 4, p: 2 }}>
//         <Typography variant="h5" sx={{ mb: 2 }}>Recent Activity</Typography>
//         {activities.map((a, i) => (
//           <Typography key={i}>{a.action} - {a.timestamp}</Typography>
//         ))}
//       </Card>
//     </Box>
//   );
// };

// export default AdminDashboard;
