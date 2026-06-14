import { useEffect, useState } from 'react'
import { get } from '../api'
import VideoCard from '../components/VideoCard'

export default function Home() {
  const [videos, setVideos] = useState([])
  const [error, setError] = useState('')

  useEffect(() => {
    get('/home')
      .then(setVideos)
      .catch((err) => setError(err.message || 'Failed to load videos'))
  }, [])

  return (
    <div>
      <h1 className="section-title">Recommended</h1>
      {error && <div className="card">Error: {error}</div>}
      <div className="video-feed">
        <div className="video-grid">
          {videos.map((video) => (
            <VideoCard key={video.id} video={video} />
          ))}
        </div>
      </div>
      {videos.length === 0 && !error && <p>No videos found.</p>}
    </div>
  )
}
