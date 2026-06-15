import { apiFetch } from './client.js';

export function uploadVideo(payload) {
  return apiFetch('/api/upload', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}
