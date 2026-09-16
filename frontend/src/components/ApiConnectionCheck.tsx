import { useState } from 'react'
import { fetchHealth, type HealthResponse } from '../lib/api'
import './ApiConnectionCheck.css'

type Status = 'idle' | 'loading' | 'success' | 'error'

function ApiConnectionCheck() {
  const [status, setStatus] = useState<Status>('idle')
  const [health, setHealth] = useState<HealthResponse | null>(null)
  const [error, setError] = useState<string | null>(null)

  const checkConnection = async () => {
    setStatus('loading')
    setError(null)
    try {
      const result = await fetchHealth()
      setHealth(result)
      setStatus('success')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error')
      setStatus('error')
    }
  }

  return (
    <section id="api-check">
      <h2>Backend connection</h2>
      <p>Ping the Spring Boot API to confirm the frontend can reach it.</p>
      <button type="button" className="counter" onClick={checkConnection} disabled={status === 'loading'}>
        {status === 'loading' ? 'Checking…' : 'Check API connection'}
      </button>
      {status === 'success' && health && (
        <div className="api-status api-status--ok">
          <span className="api-status__dot" />
          {health.service} is {health.status} (as of {new Date(health.timestamp).toLocaleTimeString()})
        </div>
      )}
      {status === 'error' && (
        <div className="api-status api-status--error">
          <span className="api-status__dot" />
          Could not reach backend: {error}
        </div>
      )}
    </section>
  )
}

export default ApiConnectionCheck
