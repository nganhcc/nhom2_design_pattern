import { RefreshCw } from 'lucide-react';
import { useEffect, useState } from 'react';
import * as videoApi from '../api/videoApi.js';
import VideoGrid from '../components/VideoGrid.jsx';
import { useDemo } from '../context/DemoContext.jsx';

export default function HomePage() {
  const { currentUserId, showNotice } = useDemo();
  const [videos, setVideos] = useState([]);
  const [loading, setLoading] = useState(false);

  async function loadHome() {
    setLoading(true);
    try {
      const result = await videoApi.home(currentUserId);
      setVideos(result);
    } catch (error) {
      showNotice(error.message, 'error');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadHome();
  }, [currentUserId]);

  return (
    <section className="page">
      <div className="page-header">
        <div>
          <h1>Home Feed</h1>
          <p>Recommended public videos from the backend demo data.</p>
        </div>
        <button type="button" onClick={loadHome} disabled={loading}>
          <RefreshCw size={16} />Refresh
        </button>
      </div>
      <VideoGrid videos={videos} emptyText="No videos available. Start the backend and seed data first." />
    </section>
  );
}
