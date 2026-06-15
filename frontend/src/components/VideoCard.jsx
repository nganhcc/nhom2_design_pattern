import { Eye, Heart, Timer } from 'lucide-react';
import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useDemo } from '../context/DemoContext.jsx';

export default function VideoCard({ video }) {
  const [imageFailed, setImageFailed] = useState(false);
  const { setSelectedVideoId, setSelectedChannelId } = useDemo();

  const channelId = video.channelId || video.uploaderId;
  const initial = (video.title || 'V').trim().charAt(0).toUpperCase();

  return (
    <article className="video-card">
      <Link
        className="thumb"
        to={`/videos/${video.id}`}
        onClick={() => setSelectedVideoId(video.id)}
      >
        {video.thumbnailUrl && !imageFailed ? (
          <img src={video.thumbnailUrl} alt={video.title} onError={() => setImageFailed(true)} />
        ) : (
          <span>{initial}</span>
        )}
      </Link>
      <div className="video-card-body">
        <Link
          className="video-title"
          to={`/videos/${video.id}`}
          onClick={() => setSelectedVideoId(video.id)}
        >
          {video.title}
        </Link>
        <p>{video.description || 'No description'}</p>
        <div className="meta-row">
          <span><Eye size={14} />{video.viewCount ?? 0}</span>
          <span><Heart size={14} />{video.likeCount ?? 0}</span>
          <span><Timer size={14} />{video.duration ?? 0}s</span>
        </div>
        <div className="card-footer">
          <span className="pill">{video.category || 'General'}</span>
          {channelId && (
            <Link
              to={`/channels/${channelId}`}
              onClick={() => setSelectedChannelId(channelId)}
            >
              Channel
            </Link>
          )}
        </div>
      </div>
    </article>
  );
}
