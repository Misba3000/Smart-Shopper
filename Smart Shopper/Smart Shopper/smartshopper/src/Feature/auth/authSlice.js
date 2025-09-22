import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";

// ----- Safe localStorage parsing -----
let user = null;
const storedUser = localStorage.getItem("user");

try {
  user = storedUser ? JSON.parse(storedUser) : null;
} catch (err) {
  console.error("Invalid user data in localStorage", err);
  localStorage.removeItem("user"); // Remove broken data
}

// ----- Async login example (replace with your API call if needed) -----
export const login = createAsyncThunk(
  "auth/login",
  async (credentials, thunkAPI) => {
    try {
      // Replace with your actual login API logic
      const response = { username: credentials.username }; 
      localStorage.setItem("user", JSON.stringify(response));
      return response;
    } catch (error) {
      return thunkAPI.rejectWithValue(error.message);
    }
  }
);

// ----- Slice -----
const authSlice = createSlice({
  name: "auth",
  initialState: {
    user: user,
    isLoading: false,
    error: null,
  },
  reducers: {
    logout: (state) => {
      state.user = null;
      localStorage.removeItem("user");
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(login.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(login.fulfilled, (state, action) => {
        state.isLoading = false;
        state.user = action.payload;
      })
      .addCase(login.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload;
      });
  },
});

export const { logout } = authSlice.actions;
export default authSlice.reducer;
