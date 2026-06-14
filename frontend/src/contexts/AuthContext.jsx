import { createContext, useContext, useEffect, useState } from 'react'
import { get, post } from '../api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loaded, setLoaded] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    get('/auth/me')
      .then(setUser)
      .catch(() => setUser(null))
      .finally(() => setLoaded(true))
  }, [])

  async function login(userId) {
    setError('')
    try {
      const result = await post(`/auth/login?userId=${encodeURIComponent(userId)}`)
      setUser({ id: result.userId })
      return result
    } catch (err) {
      setError(err.message || 'Login failed')
      throw err
    }
  }

  async function logout() {
    setError('')
    try {
      await post('/auth/logout')
      setUser(null)
    } catch (err) {
      setError(err.message || 'Logout failed')
      throw err
    }
  }

  return (
    <AuthContext.Provider value={{ user, loaded, error, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) throw new Error('useAuth must be used within AuthProvider')
  return context
}
