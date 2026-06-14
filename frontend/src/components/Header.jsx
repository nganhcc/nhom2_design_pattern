import { NavLink, useNavigate } from 'react-router-dom'
import { useState } from 'react'
import UserPanel from './UserPanel'
import './Header.css'

const linkClass = ({ isActive }) => ({
  color: isActive ? '#5b21b6' : '#374151',
  fontWeight: isActive ? 700 : 500,
  textDecoration: 'none',
})

export default function Header() {
  const [query, setQuery] = useState('')
  const navigate = useNavigate()

  const handleSearch = (event) => {
    event.preventDefault()
    if (query.trim()) {
      navigate(`/search?q=${encodeURIComponent(query.trim())}`)
      setQuery('')
    }
  }

  return (
    <header className="topbar">
      <div className="topbar-left">
        <div className="brand">VideoSharing</div>
      </div>
      <form className="topbar-search" onSubmit={handleSearch}>
        <input
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search"
        />
        <button className="button" type="submit">Search</button>
      </form>
      <div className="topbar-right">
        <UserPanel />
      </div>
    </header>
  )
}
