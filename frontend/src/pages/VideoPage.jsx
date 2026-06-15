import { Eye, Heart, Pause, Play, RotateCcw, SkipForward, ThumbsDown } from 'lucide-react';
import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import * as videoApi from '../api/videoApi.js';
import CommentSection from '../components/CommentSection.jsx';
import { useDemo } from '../context/DemoContext.jsx';

export default function VideoPage() {
  const { videoId } = useParams();
  const { setSelectedVideoId, setSelectedChannelId, showNotice } = useDemo();
  const [video, setVideo] = useState(null);
  const [playerState, setPlayerState] = useState('IdleState');
  const [loading, setLoading] = useState(false);

  async function loadVideo() {
    setLoading(true);
    try {
      const result = await videoApi.getVideo(videoId);
      setVideo(result);
      setSelectedVideoId(videoId);
      setSelectedChannelId(result.channelId || result.uploaderId);
    } catch (error) {
      showNotice(error.message, 'error');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadVideo();
  }, [videoId]);

  async function runAction(action, successMessage) {
    setLoading(true);
    try {
      const result = await action();
      if (result?.id) {
        setVideo(result);
      }
      showNotice(successMessage, 'success');
    } catch (error) {
      showNotice(error.status === 401 ? 'Please login before this interaction' : error.message, 'error');
    } finally {
      setLoading(false);
    }
  }

  async function runPlayer(action, params) {
    try {
      const result = await videoApi.playerAction(videoId, action, params);
      setPlayerState(result.state);
    } catch (error) {
      showNotice(error.message, 'error');
    }
  }

  if (!video) {
    return <div className="empty-state">{loading ? 'Loading video...' : 'Video not found.'}</div>;
  }

  const channelId = video.channelId || video.uploaderId;

  return (
    <section className="page">
      <div className="video-detail">
        <div className="video-main">
          <div className="player-box">
            <span>{playerState}</span>
          </div>
          <div className="page-header">
            <div>
              <h1>{video.title}</h1>
              <p>{video.description || 'No description'}</p>
            </div>
            {channelId && <Link className="button-link" to={`/channels/${channelId}`}>Open Channel</Link>}
          </div>
          <div className="meta-row large">
            <span><Eye size={16} />{video.viewCount ?? 0} views</span>
            <span><Heart size={16} />{video.likeCount ?? 0} likes</span>
            <span>{video.category}</span>
          </div>
          <div className="action-bar">
            <button type="button" onClick={() => runAction(() => videoApi.recordView(videoId), 'View recorded')}>
              <Eye size={16} />View
            </button>
            <button type="button" onClick={() => runAction(() => videoApi.likeVideo(videoId), 'Liked')}>
              <Heart size={16} />Like
            </button>
            <button type="button" onClick={() => runAction(() => videoApi.dislikeVideo(videoId), 'Disliked')}>
              <ThumbsDown size={16} />Dislike
            </button>
            <button type="button" onClick={() => runAction(() => videoApi.undoInteraction(), 'Undo complete').then(loadVideo)}>
              <RotateCcw size={16} />Undo
            </button>
          </div>
          <div className="action-bar">
            <button type="button" onClick={() => runPlayer('play')}><Play size={16} />Play</button>
            <button type="button" onClick={() => runPlayer('pause')}><Pause size={16} />Pause</button>
            <button type="button" onClick={() => runPlayer('seek', { timeMs: 30000 })}><SkipForward size={16} />Seek 30s</button>
            <button type="button" onClick={() => runPlayer('end')}>End</button>
          </div>
        </div>
        <CommentSection videoId={videoId} />
      </div>
    </section>
  );
}
