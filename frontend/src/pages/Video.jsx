import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { get, postNoBody } from '../api'
import CommentList from '../components/CommentList'

export default function Video() {
  const { id } = useParams()
  const [video, setVideo] = useState(null)
  const [error, setError] = useState('')
  const [commandMessage, setCommandMessage] = useState('')
  const [stateMessage, setStateMessage] = useState('')
  const [undoMessage, setUndoMessage] = useState('')

  useEffect(() => {
    get(`/videos/${id}`)
      .then(setVideo)
      .catch((err) => setError(err.message || 'Failed to load video'))
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
    <div>
      <h1 className="section-title">{video.title}</h1>
      <div className="card">
      <section className="pattern-panel">
        <h2>State Pattern</h2>
        <p>Trạng thái phát video được quản lý bởi State Pattern ở backend.</p>
        <div className="status-row">
          <span><strong>Player state:</strong> {playerState}</span>
          <span>{stateMessage || 'No playback action yet'}</span>
        </div>
        <div className="button-row">
          <button className="button small" onClick={() => handlePlayback(`/videos/${id}/play`, null, 'Play')}>Play</button>
          <button className="button small" onClick={() => handlePlayback(`/videos/${id}/pause`, null, 'Pause')}>Pause</button>
          <button className="button small" onClick={() => handlePlayback(`/videos/${id}/seek`, { timeMs: 60000 }, 'Seek 60s')}>Seek 60s</button>
          <button className="button small" onClick={() => handlePlayback(`/videos/${id}/end`, null, 'End')}>End</button>
        </div>
      </section>
        <div>{video.description}</div>
        <div>
          <strong>Channel:</strong>{' '}
          <Link to={`/channel/${video.channelId}`}>{video.channelId}</Link>
        </div>
        <div><strong>Views:</strong> {video.viewCount}</div>
        <div><strong>Likes:</strong> {video.likeCount}</div>
        <div><strong>Category:</strong> {video.category}</div>
      </div>

      <section className="pattern-panel">
        <h2>Command Pattern</h2>
        <p>View / Like / Dislike được gửi dưới dạng Command và lưu lịch sử trong backend.</p>
        <div className="button-row">
          <button className="button small" onClick={() => executeCommand(`/videos/${id}/view`, 'View')}>Send View Command</button>
          <button className="button small" onClick={() => executeCommand(`/videos/${id}/like`, 'Like')}>Send Like Command</button>
          <button className="button small" onClick={() => executeCommand(`/videos/${id}/dislike`, 'Dislike')}>Send Dislike Command</button>
          <button className="button small" onClick={handleUndo}>Undo Command</button>
        </div>
        {commandMessage && <div className="pattern-note">{commandMessage}</div>}
        {undoMessage && <div className="pattern-note">{undoMessage}</div>}
      </section>


      <CommentList videoId={id} />
    </div>
  )
}
