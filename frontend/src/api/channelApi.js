import { apiFetch, buildQuery } from './client.js';

export function getChannel(channelId, viewerId) {
  return apiFetch(`/api/channels/${encodeURIComponent(channelId)}${buildQuery({ viewerId })}`);
}

export function subscribe(channelId) {
  return apiFetch(`/api/channels/${encodeURIComponent(channelId)}/subscribe`, { method: 'POST' });
}

export function unsubscribe(channelId) {
  return apiFetch(`/api/channels/${encodeURIComponent(channelId)}/unsubscribe`, { method: 'POST' });
}
