

// Feature/store.js
import { configureStore } from "@reduxjs/toolkit";
import authReducer from "./Feature/auth/authSlice";

const store = configureStore({
  reducer: {
    auth: authReducer,
    // add other slices here if you have
  },
});

export default store; // ✅ important!

