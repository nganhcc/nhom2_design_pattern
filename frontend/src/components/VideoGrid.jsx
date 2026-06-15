import VideoCard from './VideoCard.jsx';

export default function VideoGrid({ videos, emptyText = 'No videos found.' }) {
  if (!videos?.length) {
    return <div className="empty-state">{emptyText}</div>;
  }

  return (
    <div className="video-grid">
      {videos.map((video) => (
        <VideoCard key={video.id} video={video} />
      ))}
    </div>
  );
}
