import { Search } from 'lucide-react';
import { useState } from 'react';
import * as videoApi from '../api/videoApi.js';
import VideoGrid from '../components/VideoGrid.jsx';
import { useDemo } from '../context/DemoContext.jsx';

const initialFilters = {
  q: '',
  sort: 'relevance',
  category: '',
  minDuration: '',
  maxDuration: '',
  from: '',
  to: ''
};

export default function SearchPage() {
  const { showNotice } = useDemo();
  const [filters, setFilters] = useState(initialFilters);
  const [videos, setVideos] = useState([]);
  const [searched, setSearched] = useState(false);
  const [loading, setLoading] = useState(false);

  function updateFilter(key, value) {
    setFilters((current) => ({ ...current, [key]: value }));
  }

  async function handleSearch(event) {
    event.preventDefault();
    setLoading(true);
    try {
      const result = await videoApi.searchVideos(filters);
      setVideos(result);
      setSearched(true);
      showNotice(`Found ${result.length} result(s)`, 'success');
    } catch (error) {
      showNotice(error.message, 'error');
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="page">
      <div className="page-header">
        <div>
          <h1>Search & Filters</h1>
          <p>TV1 demo: Strategy, Decorator and Template Method.</p>
        </div>
      </div>

      <form className="search-form panel" onSubmit={handleSearch}>
        <label>
          Keyword
          <input value={filters.q} onChange={(event) => updateFilter('q', event.target.value)} placeholder="builder, pattern, spring" />
        </label>
        <label>
          Sort
          <select value={filters.sort} onChange={(event) => updateFilter('sort', event.target.value)}>
            <option value="relevance">Relevance</option>
            <option value="date">Date</option>
            <option value="views">Views</option>
          </select>
        </label>
        <label>
          Category
          <input value={filters.category} onChange={(event) => updateFilter('category', event.target.value)} placeholder="Design Patterns" />
        </label>
        <label>
          Min duration
          <input type="number" value={filters.minDuration} onChange={(event) => updateFilter('minDuration', event.target.value)} placeholder="100" />
        </label>
        <label>
          Max duration
          <input type="number" value={filters.maxDuration} onChange={(event) => updateFilter('maxDuration', event.target.value)} placeholder="500" />
        </label>
        <label>
          From
          <input value={filters.from} onChange={(event) => updateFilter('from', event.target.value)} placeholder="2026-06-12T00:00" />
        </label>
        <label>
          To
          <input value={filters.to} onChange={(event) => updateFilter('to', event.target.value)} placeholder="2026-06-14T23:59" />
        </label>
        <button type="submit" disabled={loading}>
          <Search size={16} />Search
        </button>
      </form>

      {searched && <VideoGrid videos={videos} emptyText="No matching videos." />}
    </section>
  );
}
