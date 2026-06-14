import { NavLink } from 'react-router-dom'
import UserPanel from './UserPanel'
import './Header.css'

const linkStyle = ({ isActive }) => ({
  color: isActive ? '#5b21b6' : '#374151',
  fontWeight: isActive ? 700 : 500,
  textDecoration: 'none',
  marginRight: 18,
})

export default function Header() {
  return (
    <header className="topbar">
      <div className="brand">VideoSharing</div>
      <nav>
        <NavLink to="/" style={linkStyle} end>
          Home
        </NavLink>
        <NavLink to="/search" style={linkStyle}>
          Search
        </NavLink>
        <NavLink to="/upload" style={linkStyle}>
          Upload
        </NavLink>
        <NavLink to="/auth" style={linkStyle}>
          Auth
        </NavLink>
      </nav>
      <UserPanel />
    </header>
  )
}
