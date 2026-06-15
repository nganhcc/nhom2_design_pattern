import { apiFetch, buildQuery } from './client.js';

export function login(userId) {
  return apiFetch(`/api/auth/login${buildQuery({ userId })}`, { method: 'POST' });
}

export function logout() {
  return apiFetch('/api/auth/logout', { method: 'POST' });
}

export function me() {
  return apiFetch('/api/auth/me');
}
