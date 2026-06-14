import { useAuth } from '../contexts/AuthContext'

export default function UserPanel() {
  const { user, loaded, error, logout } = useAuth()

  if (!loaded) return <div className="topbar-note">Loading user...</div>

  return (
    <div className="user-panel">
      {user ? (
        <>
          <span>Welcome, <strong>{user.id}</strong></span>
          <button className="button outline" onClick={logout}>Logout</button>
        </>
      ) : (
        <span>You are not logged in.</span>
      )}
      {error && <div className="error-box" style={{ marginTop: 8 }}>{error}</div>}
    </div>
  )
}
