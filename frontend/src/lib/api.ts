import { env } from '../config/env'

const API_BASE_URL = env.apiBaseUrl

export interface HealthResponse {
  status: string
  service: string
  timestamp: string
}

export async function fetchHealth(): Promise<HealthResponse> {
  const response = await fetch(`${API_BASE_URL}/health`)
  if (!response.ok) {
    throw new Error(`Backend responded with ${response.status}`)
  }
  return response.json()
}
