import { MessageSquare, RefreshCw, Send, Trash2 } from 'lucide-react';
import { useEffect, useState } from 'react';
import * as commentApi from '../api/commentApi.js';
import { useDemo } from '../context/DemoContext.jsx';

export default function CommentSection({ videoId }) {
  const { showNotice } = useDemo();
  const [view, setView] = useState('threaded');
  const [comments, setComments] = useState([]);
  const [text, setText] = useState('');
  const [replyTextById, setReplyTextById] = useState({});
  const [loading, setLoading] = useState(false);

  async function loadComments(nextView = view) {
    setLoading(true);
    try {
      const result = await commentApi.getComments(videoId, nextView);
      setComments(result);
    } catch (error) {
      showNotice(error.message, 'error');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    if (videoId) loadComments(view);
  }, [videoId, view]);

  async function submitComment(event) {
    event.preventDefault();
    try {
      await commentApi.createComment({ videoId, text });
      setText('');
      showNotice('Comment created', 'success');
      await loadComments();
    } catch (error) {
      showNotice(error.status === 401 ? 'Login before commenting' : error.message, 'error');
    }
  }

  async function submitReply(parentId) {
    const replyText = replyTextById[parentId] || '';
    try {
      await commentApi.createComment({ videoId, text: replyText, parentId });
      setReplyTextById((current) => ({ ...current, [parentId]: '' }));
      showNotice('Reply created', 'success');
      await loadComments();
    } catch (error) {
      showNotice(error.status === 401 ? 'Login before replying' : error.message, 'error');
    }
  }

  async function deleteComment(commentId) {
    try {
      await commentApi.deleteComment(commentId);
      showNotice('Comment deleted', 'info');
      await loadComments();
    } catch (error) {
      showNotice(error.message, 'error');
    }
  }

  return (
    <section className="panel">
      <div className="panel-header">
        <h2><MessageSquare size={20} />Comments</h2>
        <div className="button-row">
          <select value={view} onChange={(event) => setView(event.target.value)}>
            <option value="threaded">Threaded</option>
            <option value="flat">Flat</option>
          </select>
          <button type="button" onClick={() => loadComments()} disabled={loading}>
            <RefreshCw size={16} />Refresh
          </button>
        </div>
      </div>

      <form className="comment-form" onSubmit={submitComment}>
        <textarea
          value={text}
          onChange={(event) => setText(event.target.value)}
          placeholder="Write a comment"
        />
        <button type="submit"><Send size={16} />Post</button>
      </form>

      <div className="comment-list">
        {comments.length === 0 && <div className="empty-state">No comments yet.</div>}
        {comments.map((comment) => (
          <CommentItem
            key={comment.id}
            comment={comment}
            replyTextById={replyTextById}
            setReplyTextById={setReplyTextById}
            onReply={submitReply}
            onDelete={deleteComment}
          />
        ))}
      </div>
    </section>
  );
}

function CommentItem({ comment, replyTextById, setReplyTextById, onReply, onDelete }) {
  const replyText = replyTextById[comment.id] || '';

  return (
    <article className="comment-item">
      <div className="comment-content">
        <strong>{comment.authorId || 'anonymous'}</strong>
        <span>{comment.text}</span>
        <small>{comment.createdAt}</small>
      </div>
      <div className="button-row">
        <input
          value={replyText}
          onChange={(event) => setReplyTextById((current) => ({ ...current, [comment.id]: event.target.value }))}
          placeholder="reply"
        />
        <button type="button" onClick={() => onReply(comment.id)}><Send size={14} />Reply</button>
        <button type="button" onClick={() => onDelete(comment.id)}><Trash2 size={14} />Delete</button>
      </div>
      {comment.replies?.length > 0 && (
        <div className="comment-replies">
          {comment.replies.map((reply) => (
            <CommentItem
              key={reply.id}
              comment={reply}
              replyTextById={replyTextById}
              setReplyTextById={setReplyTextById}
              onReply={onReply}
              onDelete={onDelete}
            />
          ))}
        </div>
      )}
    </article>
  );
}
