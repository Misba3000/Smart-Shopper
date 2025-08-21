import React from "react";
import { Routes, Route } from "react-router-dom";
import LandingPage from "./Pages/Landing/LandingPage";
import HomePage from "./Pages/Home/HomePage";
import Navbar from "./Components/Navbar";
import Footer from "./Components/Footer";
import PrivateRoute from "./PrivateRoute";

// Protected pages
const WishlistPage = () => <h2>Wishlist Page (Protected)</h2>;
const ProfilePage = () => <h2>Profile Page (Protected)</h2>;
const ProductDetailsPage = () => <h2>Product Details Page</h2>;

const App = () => {
  return (
    <>
      <Navbar />
      <Routes>
        {/* Public pages */}
        <Route path="/" element={<HomePage />} />
        <Route path="/home" element={<HomePage />} />
        <Route path="/landing" element={<LandingPage />} />

        {/* Protected routes */}
        <Route
          path="/wishlist"
          element={
            <PrivateRoute>
              <WishlistPage />
            </PrivateRoute>
          }
        />
        <Route
          path="/profile"
          element={
            <PrivateRoute>
              <ProfilePage />
            </PrivateRoute>
          }
        />
        <Route
          path="/product/:productId"
          element={
            <PrivateRoute>
              <ProductDetailsPage />
            </PrivateRoute>
          }
        />

        {/* Catch-all */}
        <Route path="*" element={<HomePage />} />
      </Routes>
      <Footer />
    </>
  );
};

export default App;
