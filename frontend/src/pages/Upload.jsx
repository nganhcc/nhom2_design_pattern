import { useState } from 'react'
import { post } from '../api'

export default function Upload() {
  const [form, setForm] = useState({
    title: '',
    description: '',
    thumbnailUrl: '',
    visibility: 'PUBLIC',
    uploaderId: '',
    videoUrl: '',
    durationSeconds: 0,
    category: '',
  })
  const [message, setMessage] = useState('')

  const handleChange = (field) => (event) => {
    setForm({ ...form, [field]: event.target.value })
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    setMessage('')
    try {
      const video = await post('/upload', form)
      setMessage(`Uploaded: ${video.title || video.id}`)
    } catch (err) {
      setMessage(err.message)
    }
  }

  return (
    <div>
      <h1 className="section-title">Upload Video</h1>
      <form className="card" onSubmit={handleSubmit}>
        <div className="input-row">
          <input value={form.title} onChange={handleChange('title')} placeholder="Title" />
          <input value={form.category} onChange={handleChange('category')} placeholder="Category" />
        </div>
        <div className="input-row">
          <input value={form.thumbnailUrl} onChange={handleChange('thumbnailUrl')} placeholder="Thumbnail URL" />
          <input value={form.videoUrl} onChange={handleChange('videoUrl')} placeholder="Video URL" />
        </div>
        <div className="input-row">
          <input value={form.uploaderId} onChange={handleChange('uploaderId')} placeholder="Uploader ID" />
          <input value={form.visibility} onChange={handleChange('visibility')} placeholder="Visibility" />
        </div>
        <div className="input-row">
          <input
            type="number"
            value={form.durationSeconds}
            onChange={handleChange('durationSeconds')}
            placeholder="Duration seconds"
          />
          <input value={form.description} onChange={handleChange('description')} placeholder="Description" />
        </div>
        <button className="button" type="submit">Upload</button>
        {message && <p>{message}</p>}
      </form>
    </div>
  )
}
