import { useState, useEffect } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { get } from '../api'

export default function Search() {
  const [query, setQuery] = useState('')
  const [sort, setSort] = useState('relevance')
  const [category, setCategory] = useState('')
  const [minDuration, setMinDuration] = useState('')
  const [maxDuration, setMaxDuration] = useState('')
  const [fromDate, setFromDate] = useState('')
  const [toDate, setToDate] = useState('')
  const [results, setResults] = useState([])
  const [error, setError] = useState('')
  const [searchParams, setSearchParams] = useSearchParams()

  useEffect(() => {
    const q = searchParams.get('q') || ''
    const sortValue = searchParams.get('sort') || 'relevance'
    const categoryValue = searchParams.get('category') || ''
    const minValue = searchParams.get('minDuration') || ''
    const maxValue = searchParams.get('maxDuration') || ''
    const fromValue = searchParams.get('from') || ''
    const toValue = searchParams.get('to') || ''

    setQuery(q)
    setSort(sortValue)
    setCategory(categoryValue)
    setMinDuration(minValue)
    setMaxDuration(maxValue)
    setFromDate(fromValue)
    setToDate(toValue)

    if (q || categoryValue || minValue || maxValue || fromValue || toValue) {
      getSearchResults({
        q,
        sort: sortValue,
        category: categoryValue,
        minDuration: minValue,
        maxDuration: maxValue,
        from: fromValue,
        to: toValue,
      })
    } else {
      setResults([])
    }
  }, [searchParams])

  async function getSearchResults(params) {
    setError('')
    try {
      const queryString = new URLSearchParams()
      if (params.q) queryString.set('q', params.q)
      if (params.sort) queryString.set('sort', params.sort)
      if (params.category) queryString.set('category', params.category)
      if (params.minDuration) queryString.set('minDuration', params.minDuration)
      if (params.maxDuration) queryString.set('maxDuration', params.maxDuration)
      if (params.from) queryString.set('from', params.from)
      if (params.to) queryString.set('to', params.to)

      const data = await get(`/search?${queryString.toString()}`)
      setResults(Array.isArray(data) ? data : [])
    } catch (err) {
      setError(err.message || 'Search failed')
    }
  }

  const handleSearch = (event) => {
    event.preventDefault()

    const params = new URLSearchParams()
    if (query.trim()) params.set('q', query.trim())
    if (sort) params.set('sort', sort)
    if (category.trim()) params.set('category', category.trim())
    if (minDuration.trim()) params.set('minDuration', minDuration.trim())
    if (maxDuration.trim()) params.set('maxDuration', maxDuration.trim())
    if (fromDate) params.set('from', fromDate)
    if (toDate) params.set('to', toDate)

    setSearchParams(params)
  }

  return (
    <div>
      <h1 className="section-title">Search</h1>
      <form className="card search-form" onSubmit={handleSearch}>
        <div className="search-row">
          <input
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search videos..."
          />
          <button className="button" type="submit">
            Search
          </button>
        </div>

        <div className="filter-grid">
          <label className="filter-group">
            <span>Sort</span>
            <select value={sort} onChange={(e) => setSort(e.target.value)}>
              <option value="relevance">Relevance</option>
              <option value="date">Date</option>
              <option value="views">Views</option>
            </select>
          </label>

          <label className="filter-group">
            <span>Category</span>
            <input
              value={category}
              onChange={(e) => setCategory(e.target.value)}
              placeholder="Category"
            />
          </label>

          <label className="filter-group">
            <span>Min Duration (sec)</span>
            <input
              type="number"
              min="0"
              value={minDuration}
              onChange={(e) => setMinDuration(e.target.value)}
              placeholder="Min"
            />
          </label>

          <label className="filter-group">
            <span>Max Duration (sec)</span>
            <input
              type="number"
              min="0"
              value={maxDuration}
              onChange={(e) => setMaxDuration(e.target.value)}
              placeholder="Max"
            />
          </label>

          <label className="filter-group">
            <span>From date</span>
            <input
              type="date"
              value={fromDate}
              onChange={(e) => setFromDate(e.target.value)}
            />
          </label>

          <label className="filter-group">
            <span>To date</span>
            <input
              type="date"
              value={toDate}
              onChange={(e) => setToDate(e.target.value)}
            />
          </label>
        </div>
      </form>

      {error && <div className="card">{error}</div>}
      <div className="video-grid">
        {results.map((video) => (
          <article className="video-card" key={video.id}>
            <Link to={`/video/${video.id}`} className="video-thumb">
              <img src={video.thumbnailUrl || 'https://via.placeholder.com/320x180?text=Thumbnail'} alt={video.title} />
              <span className="video-duration">{video.duration}s</span>
            </Link>
            <div className="video-card-body">
              <Link to={`/video/${video.id}`} className="video-title">{video.title}</Link>
              <div className="video-meta">
                <span>{video.channelId}</span>
                <span>{video.viewCount} views</span>
              </div>
            </div>
          </article>
        ))}
      </div>
      {results.length === 0 && !error && <p>No results found.</p>}
    </div>
  )
}
