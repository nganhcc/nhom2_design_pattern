import { useEffect, useState } from 'react'
import { get, post, del } from '../api'
import CommentItem from './CommentItem'

export default function CommentList({ videoId }) {
  const [comments, setComments] = useState([])
  const [text, setText] = useState('')
  const [error, setError] = useState('')

  useEffect(() => {
    get(`/comments/video/${videoId}`)
      .then(setComments)
      .catch((err) => setError(err.message || 'Failed to load comments'))
  }, [videoId])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    try {
      const comment = await post('/comments', { videoId, text, parentId: null, loggedIn: true })
      setComments((prev) => [comment, ...prev])
      setText('')
    } catch (err) {
      setError(err.message || 'Failed to post comment')
    }
  }

  const insertReply = (list, parentId, reply) => {
    return list.map((c) => {
      if (c.id === parentId) {
        const replies = c.replies ? [reply, ...c.replies] : [reply]
        return { ...c, replies }
      }
      if (c.replies && c.replies.length > 0) {
        return { ...c, replies: insertReply(c.replies, parentId, reply) }
      }
      return c
    })
  }

  const handleReply = async (parentId, replyText) => {
    setError('')
    try {
      const reply = await post('/comments', { videoId, text: replyText, parentId, loggedIn: true })
      setComments((prev) => insertReply(prev, parentId, reply))
    } catch (err) {
      setError(err.message || 'Failed to post reply')
    }
  }

  const removeComment = (list, idToRemove) => {
    return list
      .filter((c) => c.id !== idToRemove)
      .map((c) => ({
        ...c,
        replies: c.replies ? removeComment(c.replies, idToRemove) : [],
      }))
  }

  const handleDelete = async (commentId) => {
    setError('')
    try {
      await del(`/comments/${commentId}`)
      setComments((prev) => removeComment(prev, commentId))
    } catch (err) {
      setError(err.message || 'Failed to delete comment')
    }
  }

  return (
    <div className="card">
      <h2>Comments</h2>
      {error && <div className="error-box">{error}</div>}
      <form className="input-row" onSubmit={handleSubmit}>
        <input
          value={text}
          onChange={(e) => setText(e.target.value)}
          placeholder="Write a comment"
        />
        <button className="button" type="submit">Post</button>
      </form>
      <div>
        {comments.length === 0 && <p>No comments yet.</p>}
        {comments.map((comment) => (
          <CommentItem key={comment.id} comment={comment} onReply={handleReply} onDelete={handleDelete} />
        ))}
      </div>
    </div>
  )
}
