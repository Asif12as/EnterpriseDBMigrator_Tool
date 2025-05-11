import React from 'react';
import { Spinner } from 'react-bootstrap';

const LoadingOverlay = ({ show, message = 'Loading...' }) => {
  if (!show) return null;
  
  return (
    <div className="loading-overlay">
      <div className="loading-content">
        <Spinner animation="border" role="status" variant="primary" />
        <p className="mt-2">{message}</p>
      </div>
    </div>
  );
};

export default LoadingOverlay;