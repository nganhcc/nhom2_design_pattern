import { Film, Home, Search, Upload } from 'lucide-react';
import { NavLink, Outlet } from 'react-router-dom';
import { useDemo } from '../context/DemoContext.jsx';
import AuthPanel from './AuthPanel.jsx';

export default function Layout() {
  const { notice, clearNotice, selectedVideoId, selectedChannelId } = useDemo();

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <NavLink className="brand" to="/">
          <Film size={24} />
          <span>MiniTube</span>
        </NavLink>

        <nav className="nav">
          <NavLink to="/" end><Home size={18} />Home</NavLink>
          <NavLink to="/search"><Search size={18} />Search</NavLink>
          <NavLink to="/upload"><Upload size={18} />Upload</NavLink>
          {selectedVideoId && <NavLink to={`/videos/${selectedVideoId}`}>Video</NavLink>}
          {selectedChannelId && <NavLink to={`/channels/${selectedChannelId}`}>Channel</NavLink>}
        </nav>

        <AuthPanel />
      </aside>

      <main className="main">
        {notice && (
          <div className={`notice ${notice.type}`}>
            <span>{notice.message}</span>
            <button type="button" onClick={clearNotice}>Close</button>
          </div>
        )}
        <Outlet />
      </main>
    </div>
  );
}
