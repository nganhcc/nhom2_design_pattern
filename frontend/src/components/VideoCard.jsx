import { Link } from 'react-router-dom'

export default function VideoCard({ video }) {
  return (
    <article className="video-card">
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
  )
}
