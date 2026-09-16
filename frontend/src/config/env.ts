interface EnvSchema {
  apiBaseUrl: string
  mode: string
  isDev: boolean
  isProd: boolean
}

function readEnv(): EnvSchema {
  const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api'

  return {
    apiBaseUrl,
    mode: import.meta.env.MODE,
    isDev: import.meta.env.DEV,
    isProd: import.meta.env.PROD,
  }
}

export const env = readEnv()
