import React, { useState, useEffect } from 'react';
import { Table, Card, Badge } from 'react-bootstrap';
import axios from 'axios';

const MigrationHistory = () => {
  const [migrations, setMigrations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    // Simulate loading history data
    // In a real app, this would fetch from your API
    const mockData = [
      {
        id: 1,
        sourceType: 'mysql',
        targetType: 'postgresql',
        startTime: '2023-01-15 10:30:45',
        endTime: '2023-01-15 10:45:12', 
        success: true,
        totalTables: 12,
        tablesCreated: 12,
        totalRowsMigrated: 5842
      },
      {
        id: 2,
        sourceType: 'postgresql',
        targetType: 'oracle',
        startTime: '2023-01-14 15:22:30',
        endTime: '2023-01-14 15:38:05', 
        success: true,
        totalTables: 8,
        tablesCreated: 8,
        totalRowsMigrated: 3219
      },
      {
        id: 3,
        sourceType: 'mysql',
        targetType: 'sqlserver',
        startTime: '2023-01-13 09:15:00',
        endTime: '2023-01-13 09:16:45', 
        success: false,
        errorMessage: 'Connection refused: connect',
        totalTables: 5,
        tablesCreated: 2,
        totalRowsMigrated: 150
      }
    ];
    
    setTimeout(() => {
      setMigrations(mockData);
      setLoading(false);
    }, 1000);
    
    // In a real application, you would use:
    // axios.get('/api/migrations/history')
    //   .then(response => {
    //     setMigrations(response.data);
    //     setLoading(false);
    //   })
    //   .catch(err => {
    //     setError('Failed to load migration history');
    //     setLoading(false);
    //   });
  }, []);

  if (loading) return <div className="text-center mt-5">Loading history...</div>;
  if (error) return <div className="text-center mt-5 text-danger">{error}</div>;

  return (
    <div className="container mt-5">
      <Card>
        <Card.Header as="h4">Migration History</Card.Header>
        <Card.Body>
          <Table striped bordered hover responsive>
            <thead>
              <tr>
                <th>ID</th>
                <th>Source</th>
                <th>Target</th>
                <th>Start Time</th>
                <th>Duration</th>
                <th>Status</th>
                <th>Tables</th>
                <th>Rows</th>
              </tr>
            </thead>
            <tbody>
              {migrations.map(migration => {
                const startDate = new Date(migration.startTime);
                const endDate = new Date(migration.endTime);
                const durationInSeconds = Math.round((endDate - startDate) / 1000);
                
                return (
                  <tr key={migration.id}>
                    <td>{migration.id}</td>
                    <td>{migration.sourceType}</td>
                    <td>{migration.targetType}</td>
                    <td>{migration.startTime}</td>
                    <td>{durationInSeconds} seconds</td>
                    <td>
                      {migration.success ? 
                        <Badge bg="success">Success</Badge> : 
                        <Badge bg="danger">Failed</Badge>}
                    </td>
                    <td>{migration.tablesCreated} / {migration.totalTables}</td>
                    <td>{migration.totalRowsMigrated}</td>
                  </tr>
                );
              })}
            </tbody>
          </Table>
        </Card.Body>
      </Card>
    </div>
  );
};

export default MigrationHistory;