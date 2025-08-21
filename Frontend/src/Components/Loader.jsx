import React from "react";
import { CircularProgress, Box, Typography } from "@mui/material";

const Loader = ({ text = "Loading..." }) => {
  return (
    <Box
      sx={{
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        py: 5,
      }}
    >
      <CircularProgress color="primary" />
      <Typography variant="body1" mt={2}>
        {text}
      </Typography>
    </Box>
  );
};

export default Loader;
