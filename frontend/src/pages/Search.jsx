import { useState } from 'react'
import { Link } from 'react-router-dom'
import { get } from '../api'

export default function Search() {
  const [query, setQuery] = useState('')
  const [results, setResults] = useState([])
  const [error, setError] = useState('')

  async function handleSearch(event) {
    event.preventDefault()
    setError('')
    try {
      const data = await get(`/search?q=${encodeURIComponent(query)}`)
      setResults(Array.isArray(data) ? data : [])
    } catch (err) {
      setError(err.message || 'Search failed')
    }
  }

  return (
    <div>
      <h1 className="section-title">Search</h1>
      <form className="card input-row" onSubmit={handleSearch}>
        <input
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search videos..."
        />
        <button className="button" type="submit">
          Search
        </button>
      </form>
      {error && <div className="card">{error}</div>}
      <div className="grid grid-cols-3">
        {results.map((video) => (
          <article className="card" key={video.id}>
            <Link to={`/video/${video.id}`}><strong>{video.title}</strong></Link>
            <div>{video.category || 'Uncategorized'}</div>
            <div>Views: {video.viewCount}</div>
          </article>
        ))}
      </div>
    </div>
  )
}
