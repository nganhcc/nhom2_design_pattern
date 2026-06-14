import { useState } from 'react'

export default function CommentItem({ comment, level = 0, onReply, onDelete }) {
  const [showReply, setShowReply] = useState(false)
  const [replyText, setReplyText] = useState('')

  const submitReply = (e) => {
    e.preventDefault()
    if (!replyText) return
    onReply(comment.id, replyText)
    setReplyText('')
    setShowReply(false)
  }

  const handleDelete = () => {
    if (confirm('Delete this comment?')) {
      onDelete(comment.id)
    }
  }

  return (
    <div className="comment-item" style={{ marginLeft: `${level * 20}px` }}>
      <div className="comment-meta">
        <strong>{comment.authorId || 'Anonymous'}</strong>
        <small>{comment.createdAt}</small>
      </div>
      <div>{comment.text}</div>
      <div style={{ marginTop: 8, display: 'flex', gap: 8 }}>
        <button className="button small" onClick={() => setShowReply((s) => !s)}>{showReply ? 'Cancel' : 'Reply'}</button>
        <button className="button small danger" onClick={handleDelete}>Delete</button>
      </div>
      {showReply && (
        <form className="input-row" onSubmit={submitReply} style={{ marginTop: 8 }}>
          <input value={replyText} onChange={(e) => setReplyText(e.target.value)} placeholder="Write a reply" />
          <button className="button" type="submit">Send</button>
        </form>
      )}
      {comment.replies?.length > 0 && (
        <div className="comment-replies">
          {comment.replies.map((reply) => (
            <CommentItem key={reply.id} comment={reply} level={level + 1} onReply={onReply} onDelete={onDelete} />
          ))}
        </div>
      )}
    </div>
  )
}
