import { createContext, useContext, useState, useCallback } from 'react'
import client from '../api/client'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('ledger_user')
    return stored ? JSON.parse(stored) : null
  })
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  const login = useCallback(async (email, password) => {
    setLoading(true)
    setError(null)
    try {
      const { data } = await client.post('/api/auth/login', { email, password })
      const loggedInUser = { email: data.email, role: data.role }
      localStorage.setItem('ledger_token', data.token)
      localStorage.setItem('ledger_user', JSON.stringify(loggedInUser))
      setUser(loggedInUser)
      return true
    } catch (err) {
      const message =
        err.response?.status === 401
          ? 'Incorrect email or password.'
          : 'Could not reach the server. Check that the backend is running.'
      setError(message)
      return false
    } finally {
      setLoading(false)
    }
  }, [])

  const logout = useCallback(() => {
    localStorage.removeItem('ledger_token')
    localStorage.removeItem('ledger_user')
    setUser(null)
  }, [])

  return (
    <AuthContext.Provider value={{ user, login, logout, error, loading }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
