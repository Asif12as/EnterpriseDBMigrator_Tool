import React, { useState } from 'react';
import { Formik, Form, Field, ErrorMessage } from 'formik';
import * as Yup from 'yup';
import axios from 'axios';
import { Card, Alert, Spinner } from 'react-bootstrap';

const MigrationForm = () => {
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);

  const initialValues = {
    sourceType: 'mysql',
    sourceHost: 'localhost',
    sourcePort: 3306,
    sourceDatabase: '',
    sourceSchema: '',
    sourceUsername: '',
    sourcePassword: '',
    targetType: 'postgresql',
    targetHost: 'localhost',
    targetPort: 5432,
    targetDatabase: '',
    targetSchema: '',
    targetUsername: '',
    targetPassword: '',
    includeData: true,
    batchSize: 1000,
    validateData: true
  };

  const validationSchema = Yup.object({
    sourceType: Yup.string().required('Required'),
    sourceHost: Yup.string().required('Required'),
    sourcePort: Yup.number().required('Required').positive().integer(),
    sourceDatabase: Yup.string().required('Required'),
    sourceUsername: Yup.string().required('Required'),
    sourcePassword: Yup.string().required('Required'),
    targetType: Yup.string().required('Required'),
    targetHost: Yup.string().required('Required'),
    targetPort: Yup.number().required('Required').positive().integer(),
    targetDatabase: Yup.string().required('Required'),
    targetUsername: Yup.string().required('Required'),
    targetPassword: Yup.string().required('Required'),
    batchSize: Yup.number().positive().integer()
  });

  const onSubmit = async (values) => {
    setLoading(true);
    try {
      const response = await axios.post('/api/migrations', values);
      setResult(response.data);
    } catch (error) {
      console.error('Migration failed:', error);
      setResult({ 
        success: false, 
        errorMessage: error.response?.data?.errorMessage || 'Migration failed' 
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mt-5">
      <Card>
        <Card.Header as="h4">Database Migration</Card.Header>
        <Card.Body>
          <Formik
            initialValues={initialValues}
            validationSchema={validationSchema}
            onSubmit={onSubmit}
          >
            {({ isSubmitting }) => (
              <Form>
                <div className="row">
                  <div className="col-md-6">
                    <h5 className="mb-3">Source Database</h5>
                    <div className="mb-3">
                      <label htmlFor="sourceType" className="form-label">Database Type</label>
                      <Field as="select" name="sourceType" className="form-select">
                        <option value="mysql">MySQL</option>
                        <option value="postgresql">PostgreSQL</option>
                        <option value="oracle">Oracle</option>
                        <option value="sqlserver">SQL Server</option>
                      </Field>
                    </div>
                    
                    <div className="mb-3">
                      <label htmlFor="sourceHost" className="form-label">Host</label>
                      <Field name="sourceHost" type="text" className="form-control" />
                      <ErrorMessage name="sourceHost" component="div" className="text-danger" />
                    </div>
                    
                    <div className="mb-3">
                      <label htmlFor="sourcePort" className="form-label">Port</label>
                      <Field name="sourcePort" type="number" className="form-control" />
                      <ErrorMessage name="sourcePort" component="div" className="text-danger" />
                    </div>
                    
                    <div className="mb-3">
                      <label htmlFor="sourceDatabase" className="form-label">Database</label>
                      <Field name="sourceDatabase" type="text" className="form-control" />
                      <ErrorMessage name="sourceDatabase" component="div" className="text-danger" />
                    </div>
                    
                    <div className="mb-3">
                      <label htmlFor="sourceSchema" className="form-label">Schema</label>
                      <Field name="sourceSchema" type="text" className="form-control" />
                    </div>
                    
                    <div className="mb-3">
                      <label htmlFor="sourceUsername" className="form-label">Username</label>
                      <Field name="sourceUsername" type="text" className="form-control" />
                      <ErrorMessage name="sourceUsername" component="div" className="text-danger" />
                    </div>
                    
                    <div className="mb-3">
                      <label htmlFor="sourcePassword" className="form-label">Password</label>
                      <Field name="sourcePassword" type="password" className="form-control" />
                      <ErrorMessage name="sourcePassword" component="div" className="text-danger" />
                    </div>
                  </div>
                  
                  <div className="col-md-6">
                    <h5 className="mb-3">Target Database</h5>
                    <div className="mb-3">
                      <label htmlFor="targetType" className="form-label">Database Type</label>
                      <Field as="select" name="targetType" className="form-select">
                        <option value="mysql">MySQL</option>
                        <option value="postgresql">PostgreSQL</option>
                        <option value="oracle">Oracle</option>
                        <option value="sqlserver">SQL Server</option>
                      </Field>
                    </div>
                    
                    <div className="mb-3">
                      <label htmlFor="targetHost" className="form-label">Host</label>
                      <Field name="targetHost" type="text" className="form-control" />
                      <ErrorMessage name="targetHost" component="div" className="text-danger" />
                    </div>
                    
                    <div className="mb-3">
                      <label htmlFor="targetPort" className="form-label">Port</label>
                      <Field name="targetPort" type="number" className="form-control" />
                      <ErrorMessage name="targetPort" component="div" className="text-danger" />
                    </div>
                    
                    <div className="mb-3">
                      <label htmlFor="targetDatabase" className="form-label">Database</label>
                      <Field name="targetDatabase" type="text" className="form-control" />
                      <ErrorMessage name="targetDatabase" component="div" className="text-danger" />
                    </div>
                    
                    <div className="mb-3">
                      <label htmlFor="targetSchema" className="form-label">Schema</label>
                      <Field name="targetSchema" type="text" className="form-control" />
                    </div>
                    
                    <div className="mb-3">
                      <label htmlFor="targetUsername" className="form-label">Username</label>
                      <Field name="targetUsername" type="text" className="form-control" />
                      <ErrorMessage name="targetUsername" component="div" className="text-danger" />
                    </div>
                    
                    <div className="mb-3">
                      <label htmlFor="targetPassword" className="form-label">Password</label>
                      <Field name="targetPassword" type="password" className="form-control" />
                      <ErrorMessage name="targetPassword" component="div" className="text-danger" />
                    </div>
                  </div>
                </div>
                
                <h5 className="mb-3 mt-3">Migration Options</h5>
                <div className="row">
                  <div className="col-md-4">
                    <div className="form-check mb-3">
                      <Field name="includeData" type="checkbox" className="form-check-input" id="includeData" />
                      <label htmlFor="includeData" className="form-check-label">Include Data</label>
                    </div>
                  </div>
                  
                  <div className="col-md-4">
                    <div className="form-check mb-3">
                      <Field name="validateData" type="checkbox" className="form-check-input" id="validateData" />
                      <label htmlFor="validateData" className="form-check-label">Validate Data</label>
                    </div>
                  </div>
                  
                  <div className="col-md-4">
                    <div className="mb-3">
                      <label htmlFor="batchSize" className="form-label">Batch Size</label>
                      <Field name="batchSize" type="number" className="form-control" />
                    </div>
                  </div>
                </div>
                
                <button type="submit" className="btn btn-primary" disabled={isSubmitting || loading}>
                  {loading ? (
                    <>
                      <Spinner as="span" animation="border" size="sm" role="status" aria-hidden="true" />
                      &nbsp;Migrating...
                    </>
                  ) : 'Start Migration'}
                </button>
              </Form>
            )}
          </Formik>
          
          {result && (
            <Alert className="mt-4" variant={result.success ? 'success' : 'danger'}>
              <Alert.Heading>{result.success ? 'Migration Successful' : 'Migration Failed'}</Alert.Heading>
              {result.success ? (
                <div>
                  <p>Tables migrated: {result.tablesCreated} of {result.totalTables}</p>
                  <p>Rows migrated: {result.totalRowsMigrated}</p>
                  <p>Duration: {result.durationInSeconds} seconds</p>
                </div>
              ) : (
                <p>{result.errorMessage}</p>
              )}
            </Alert>
          )}
        </Card.Body>
      </Card>
    </div>
  );
};

export default MigrationForm;