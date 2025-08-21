// src/app/store.js
import { configureStore } from "@reduxjs/toolkit";
import authReducer from "../Feature/auth/authSlice";
import wishlistReducer from "../Feature/wishlist/wishlistSlice";

export const store = configureStore({
  reducer: {
    auth: authReducer,
    wishlist: wishlistReducer,
  },
});
