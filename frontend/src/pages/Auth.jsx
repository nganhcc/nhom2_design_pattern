import { useState } from 'react'
import { useAuth } from '../contexts/AuthContext'

export default function Auth() {
  const { user, loaded, login } = useAuth()
  const [userId, setUserId] = useState('')
  const [message, setMessage] = useState('')

  const handleLogin = async (event) => {
    event.preventDefault()
    setMessage('')
    try {
      const response = await login(userId)
      setMessage(`Logged in as ${response.userId}`)
    } catch (err) {
      setMessage(err.message || 'Login failed')
    }
  }

  return (
    <div>
      <h1 className="section-title">Authentication</h1>
      {!loaded ? (
        <p>Checking login state...</p>
      ) : user ? (
        <div className="card">You are already logged in as <strong>{user.id}</strong>.</div>
      ) : (
        <form className="card input-row" onSubmit={handleLogin}>
          <input
            value={userId}
            onChange={(e) => setUserId(e.target.value)}
            placeholder="User ID"
          />
          <button className="button" type="submit">Login</button>
          {message && <div className="error-box">{message}</div>}
        </form>
      )}
    </div>
  )
}
