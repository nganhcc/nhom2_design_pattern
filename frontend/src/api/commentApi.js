import { apiFetch, buildQuery } from './client.js';

export function getComments(videoId, view) {
  return apiFetch(`/api/comments/video/${encodeURIComponent(videoId)}${buildQuery({ view })}`);
}

export function createComment(payload) {
  return apiFetch('/api/comments', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function deleteComment(commentId) {
  return apiFetch(`/api/comments/${encodeURIComponent(commentId)}`, { method: 'DELETE' });
}
