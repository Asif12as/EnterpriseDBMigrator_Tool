import React from 'react';
import { Card, Button, Row, Col } from 'react-bootstrap';
import { Link } from 'react-router-dom';

const Home = () => {
  return (
    <div className="container mt-5">
      <div className="jumbotron bg-light p-5 rounded mb-4">
        <h1>Enterprise Database Migration Tool</h1>
        <p className="lead">
          A comprehensive tool for migrating data between different database systems with advanced features.
        </p>
        <hr className="my-4" />
        <p>
          Start a new migration, view history, or check documentation to get started.
        </p>
        <Button as={Link} to="/migrations" variant="primary" size="lg" className="me-3">
          New Migration
        </Button>
        <Button as={Link} to="/history" variant="secondary" size="lg">
          View History
        </Button>
      </div>

      <Row className="mt-4">
        <Col md={4}>
          <Card className="mb-4 h-100">
            <Card.Body>
              <Card.Title>Multi-Database Support</Card.Title>
              <Card.Text>
                Migrate between MySQL, PostgreSQL, Oracle DB, and SQL Server with
                automated schema translation and data type mapping.
              </Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="mb-4 h-100">
            <Card.Body>
              <Card.Title>Performance Optimization</Card.Title>
              <Card.Text>
                Batch processing and parallel execution for large datasets,
                ensuring fast and efficient migrations.
              </Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="mb-4 h-100">
            <Card.Body>
              <Card.Title>Data Validation</Card.Title>
              <Card.Text>
                Ensure data integrity during migration with comprehensive
                validation rules and detailed error reporting.
              </Card.Text>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default Home;