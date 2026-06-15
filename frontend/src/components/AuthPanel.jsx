import { LogIn, LogOut, UserCheck } from 'lucide-react';
import { useState } from 'react';
import * as authApi from '../api/authApi.js';
import { useDemo } from '../context/DemoContext.jsx';

export default function AuthPanel() {
  const { currentUserId, setCurrentUserId, showNotice } = useDemo();
  const [userId, setUserId] = useState(currentUserId || '');
  const [loading, setLoading] = useState(false);

  async function handleLogin(event) {
    event.preventDefault();
    setLoading(true);
    try {
      const result = await authApi.login(userId.trim());
      setCurrentUserId(result.userId);
      setUserId(result.userId);
      showNotice(`Logged in as ${result.userId}`, 'success');
    } catch (error) {
      showNotice(error.message, 'error');
    } finally {
      setLoading(false);
    }
  }

  async function handleLogout() {
    setLoading(true);
    try {
      await authApi.logout();
      setCurrentUserId('');
      showNotice('Logged out', 'info');
    } catch (error) {
      showNotice(error.message, 'error');
    } finally {
      setLoading(false);
    }
  }

  async function handleMe() {
    setLoading(true);
    try {
      const user = await authApi.me();
      setCurrentUserId(user.id);
      setUserId(user.id);
      showNotice(`Current session: ${user.id}`, 'success');
    } catch (error) {
      showNotice(error.status === 401 ? 'Please login before interactions' : error.message, 'error');
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="auth-panel">
      <div className="section-label">Demo Session</div>
      <form onSubmit={handleLogin}>
        <input
          value={userId}
          onChange={(event) => setUserId(event.target.value)}
          placeholder="user id"
        />
        <button type="submit" disabled={loading}>
          <LogIn size={16} />Login
        </button>
      </form>
      <div className="button-row">
        <button type="button" onClick={handleMe} disabled={loading}>
          <UserCheck size={16} />Me
        </button>
        <button type="button" onClick={handleLogout} disabled={loading}>
          <LogOut size={16} />Logout
        </button>
      </div>
      <p className="muted small">Active: {currentUserId || 'none'}</p>
    </section>
  );
}
