import { Upload } from 'lucide-react';
import { useState } from 'react';
import { Link } from 'react-router-dom';
import * as uploadApi from '../api/uploadApi.js';
import { useDemo } from '../context/DemoContext.jsx';

const initialForm = {
  title: '',
  description: '',
  thumbnailUrl: '',
  visibility: 'public',
  uploaderId: '',
  videoUrl: '',
  durationSeconds: '',
  category: 'Design Patterns'
};

export default function UploadPage() {
  const { currentUserId, setSelectedVideoId, showNotice } = useDemo();
  const [form, setForm] = useState(() => ({ ...initialForm, uploaderId: currentUserId || '' }));
  const [uploaded, setUploaded] = useState(null);
  const [loading, setLoading] = useState(false);

  function updateField(key, value) {
    setForm((current) => ({ ...current, [key]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setLoading(true);
    try {
      const payload = {
        ...form,
        durationSeconds: form.durationSeconds ? Number(form.durationSeconds) : null
      };
      const result = await uploadApi.uploadVideo(payload);
      setUploaded(result);
      setSelectedVideoId(result.id);
      showNotice('Video metadata uploaded', 'success');
    } catch (error) {
      showNotice(error.message, 'error');
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="page">
      <div className="page-header">
        <div>
          <h1>Upload Metadata</h1>
          <p>TV4 Builder demo. This form sends JSON metadata, not a video file.</p>
        </div>
      </div>

      <form className="upload-form panel" onSubmit={handleSubmit}>
        <label>Title<input value={form.title} onChange={(event) => updateField('title', event.target.value)} /></label>
        <label>Description<textarea value={form.description} onChange={(event) => updateField('description', event.target.value)} /></label>
        <label>Thumbnail URL<input value={form.thumbnailUrl} onChange={(event) => updateField('thumbnailUrl', event.target.value)} /></label>
        <label>Visibility
          <select value={form.visibility} onChange={(event) => updateField('visibility', event.target.value)}>
            <option value="public">public</option>
            <option value="private">private</option>
          </select>
        </label>
        <label>Uploader ID<input value={form.uploaderId} onChange={(event) => updateField('uploaderId', event.target.value)} placeholder="Use current user id or channel id" /></label>
        <label>Video URL<input value={form.videoUrl} onChange={(event) => updateField('videoUrl', event.target.value)} /></label>
        <label>Duration seconds<input type="number" value={form.durationSeconds} onChange={(event) => updateField('durationSeconds', event.target.value)} /></label>
        <label>Category<input value={form.category} onChange={(event) => updateField('category', event.target.value)} /></label>
        <button type="submit" disabled={loading}><Upload size={16} />Upload</button>
      </form>

      {uploaded && (
        <div className="panel result-panel">
          <h2>{uploaded.title}</h2>
          <p>Created video id: {uploaded.id}</p>
          <Link className="button-link" to={`/videos/${uploaded.id}`}>Open uploaded video</Link>
        </div>
      )}
    </section>
  );
}
