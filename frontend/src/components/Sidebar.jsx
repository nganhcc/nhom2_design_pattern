import { NavLink } from 'react-router-dom'
import './Sidebar.css'

const menu = [
  { label: 'Home', to: '/' },
  { label: 'Search', to: '/search' },
  { label: 'Upload', to: '/upload' },
  { label: 'Auth', to: '/auth' },
]

const categories = ['All', 'Music', 'Gaming', 'Education', 'Sports', 'News']

const linkClass = ({ isActive }) =>
  `sidebar-link${isActive ? ' active' : ''}`

export default function Sidebar() {
  return (
    <aside className="sidebar">
      <div className="sidebar-brand">VideoSharing</div>
      <nav className="sidebar-menu">
        {menu.map((item) => (
          <NavLink key={item.to} to={item.to} className={linkClass} end={item.to === '/'}>
            {item.label}
          </NavLink>
        ))}
      </nav>
      <div className="sidebar-section">
        <div className="sidebar-section-title">Categories</div>
        <div className="sidebar-chips">
          {categories.map((category) => (
            <span key={category} className="sidebar-chip">{category}</span>
          ))}
        </div>
      </div>
    </aside>
  )
}
