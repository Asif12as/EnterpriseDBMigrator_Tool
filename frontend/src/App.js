import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import NavBar from './components/NavBar';
import Home from './components/Home';
import MigrationForm from './components/MigrationForm';
import MigrationHistory from './components/MigrationHistory';
import LoadingOverlay from './components/LoadingOverlay'; // <-- new import
import './App.css';

function App() {
  return (
    <Router>
      <NavBar />
      <LoadingOverlay /> {/* <-- globally rendered overlay */}
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/migrations" element={<MigrationForm />} />
        <Route path="/history" element={<MigrationHistory />} />
      </Routes>
    </Router>
  );
}

export default App;
