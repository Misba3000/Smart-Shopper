// Initially empty array to store registered users
export let loginDetails = [
  // Example pre-registered user
  { email: "admin@example.com", password: "admin123" },
  {
    username: "user@gmail.com",
    password: "user123"
  }
];

// Function to add new user (for registration)
export const registerUser = (newUser) => {
  // Check if user already exists
  const exists = loginDetails.some((u) => u.email === newUser.email);
  if (!exists) {
    loginDetails.push(newUser);
    return true; // registration successful
  }
  return false; // user already exists
};
