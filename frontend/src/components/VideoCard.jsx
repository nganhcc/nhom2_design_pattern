import { Link } from 'react-router-dom'

export default function VideoCard({ video }) {
  return (
    <article className="card">
      <div>
        <strong>
          <Link to={`/video/${video.id}`}>{video.title}</Link>
        </strong>
      </div>
      <div>{video.category || 'Uncategorized'}</div>
      <div>Views: {video.viewCount}</div>
      <div>Likes: {video.likeCount}</div>
      <div>Duration: {video.duration}s</div>
      <div>
        <small>Channel: <Link to={`/channel/${video.channelId}`}>{video.channelId}</Link></small>
      </div>
    </article>
  )
}
