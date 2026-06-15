import { apiFetch, buildQuery } from './client.js';

export function home(viewerId) {
  return apiFetch(`/api/home${buildQuery({ viewerId })}`);
}

export function searchVideos(params) {
  return apiFetch(`/api/search${buildQuery(params)}`);
}

export function getVideo(videoId) {
  return apiFetch(`/api/videos/${encodeURIComponent(videoId)}`);
}

export function recordView(videoId) {
  return apiFetch(`/api/videos/${encodeURIComponent(videoId)}/view`, { method: 'POST' });
}

export function likeVideo(videoId) {
  return apiFetch(`/api/videos/${encodeURIComponent(videoId)}/like`, { method: 'POST' });
}

export function dislikeVideo(videoId) {
  return apiFetch(`/api/videos/${encodeURIComponent(videoId)}/dislike`, { method: 'POST' });
}

export function undoInteraction() {
  return apiFetch('/api/interactions/undo', { method: 'POST' });
}

export function playerAction(videoId, action, params = {}) {
  return apiFetch(`/api/videos/${encodeURIComponent(videoId)}/${action}${buildQuery(params)}`, { method: 'POST' });
}
