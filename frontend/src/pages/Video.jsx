import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { get, postNoBody } from '../api'
import CommentList from '../components/CommentList'
import VideoCard from '../components/VideoCard'

export default function Video() {
  const { id } = useParams()
  const [video, setVideo] = useState(null)
  const [recommendations, setRecommendations] = useState([])
  const [error, setError] = useState('')
  const [commandMessage, setCommandMessage] = useState('')
  const [stateMessage, setStateMessage] = useState('')
  const [undoMessage, setUndoMessage] = useState('')

  useEffect(() => {
    get(`/videos/${id}`)
      .then(setVideo)
      .catch((err) => setError(err.message || 'Failed to load video'))

    get(`/videos/${id}/recommendations`)
      .then(setRecommendations)
      .catch(() => {})
  }, [id])

  const [playerState, setPlayerState] = useState('idle')

  const executeCommand = async (path, label) => {
    setError('')
    setCommandMessage('')
    try {
      const updated = await postNoBody(path)
      if (updated) setVideo(updated)
      setCommandMessage(`${label} command executed`)
      return updated
    } catch (err) {
      setError(err.message || 'Action failed')
      return null
    }
  }

  const handleUndo = async () => {
    setError('')
    setUndoMessage('')
    try {
      await postNoBody('/interactions/undo')
      setUndoMessage('Undo command executed')
      setCommandMessage('Last command was undone')
    } catch (err) {
      setError(err.message || 'Undo failed')
    }
  }

  const handlePlayback = async (path, params, label) => {
    setError('')
    setStateMessage('')
    try {
      const url = params ? `${path}?${new URLSearchParams(params)}` : path
      const result = await postNoBody(url)
      if (result?.state) {
        setPlayerState(result.state)
        setStateMessage(`${label} triggered, player state is now ${result.state}`)
      } else {
        setStateMessage(`${label} triggered`)
      }
    } catch (err) {
      setError(err.message || 'Playback action failed')
    }
  }

  if (error) return <div className="card">Error: {error}</div>
  if (!video) return <div>Loading...</div>

  return (
    <div className="watch-page">
      <div className="watch-main">
        <div className="video-player card">
          <div className="video-player-placeholder">Video Player Placeholder</div>
          <div className="video-player-info">
            <h1 className="video-watch-title">{video.title}</h1>
            <div className="video-watch-meta">
              <span>{video.viewCount} views</span>
              <span>•</span>
              <span>{video.category}</span>
            </div>
          </div>
          <div className="video-actions">
            <button className="button">Like ({video.likeCount})</button>
            <button className="button outline">Dislike</button>
            <button className="button outline">Share</button>
            <button className="button outline">Save</button>
          </div>
          <div className="video-channel-card card">
            <div>
              <h3>{video.channelId}</h3>
              <p>{video.description}</p>
            </div>
            <Link to={`/channel/${video.channelId}`} className="button small">Visit Channel</Link>
          </div>
          <section className="state-panel card">
            <div className="state-panel-header">
              <div>
                <h2>Playback State</h2>
                <p>State Pattern quản lý trạng thái phát video ở backend.</p>
              </div>
              <span className="state-badge">State</span>
            </div>
            <div className="state-summary">
              <div className="state-label">Current state</div>
              <div className="state-value">{playerState}</div>
            </div>
            <div className="state-message">{stateMessage || 'No playback action yet'}</div>
            <div className="button-row command-buttons">
              <button className="button small" onClick={() => handlePlayback(`/videos/${id}/play`, null, 'Play')}>Play</button>
              <button className="button small" onClick={() => handlePlayback(`/videos/${id}/pause`, null, 'Pause')}>Pause</button>
              <button className="button small" onClick={() => handlePlayback(`/videos/${id}/seek`, { timeMs: 60000 }, 'Seek 60s')}>Seek 60s</button>
              <button className="button small outline" onClick={() => handlePlayback(`/videos/${id}/end`, null, 'End')}>End</button>
            </div>
          </section>
          <section className="command-panel card">
            <div className="command-panel-header">
              <div>
                <h2>Command Controls</h2>
                <p>Gửi hành động như View, Like, Dislike đến backend dưới dạng Command.</p>
              </div>
              <span className="command-badge">Backend Command</span>
            </div>
            <div className="button-row command-buttons">
              <button className="button small" onClick={() => executeCommand(`/videos/${id}/view`, 'View')}>View</button>
              <button className="button small" onClick={() => executeCommand(`/videos/${id}/like`, 'Like')}>Like</button>
              <button className="button small" onClick={() => executeCommand(`/videos/${id}/dislike`, 'Dislike')}>Dislike</button>
              <button className="button small outline" onClick={handleUndo}>Undo</button>
            </div>
            <div className="command-feedback">
              {commandMessage && <div className="command-note">{commandMessage}</div>}
              {undoMessage && <div className="command-note">{undoMessage}</div>}
            </div>
          </section>
          <CommentList videoId={id} />
        </div>
      </div>

      <aside className="watch-sidebar">
        <h2 className="section-title">Up next</h2>
        {recommendations.map((recommendation) => (
          <VideoCard key={recommendation.id} video={recommendation} />
        ))}
      </aside>
    </div>
  )
}
