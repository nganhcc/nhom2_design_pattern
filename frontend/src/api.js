const baseUrl = '/api'

async function request(path, options = {}) {
  const response = await fetch(`${baseUrl}${path}`, {
    credentials: 'same-origin',
    ...options,
  })
  const text = await response.text()
  if (!response.ok) {
    throw new Error(text || response.statusText)
  }
  try {
    return JSON.parse(text)
  } catch {
    return text
  }
}

export function get(path) {
  return request(path, { method: 'GET' })
}

export function post(path, body) {
  return request(path, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  })
}

export function postNoBody(path, options = {}) {
  return request(path, { method: 'POST', ...options })
}

export function put(path, body) {
  return request(path, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  })
}

export function del(path) {
  return request(path, { method: 'DELETE' })
}
