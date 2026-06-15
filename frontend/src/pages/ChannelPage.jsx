import { Bell, BellOff, RefreshCw } from 'lucide-react';
import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import * as channelApi from '../api/channelApi.js';
import VideoGrid from '../components/VideoGrid.jsx';
import { useDemo } from '../context/DemoContext.jsx';

export default function ChannelPage() {
  const { channelId } = useParams();
  const { currentUserId, setSelectedChannelId, showNotice } = useDemo();
  const [channel, setChannel] = useState(null);
  const [loading, setLoading] = useState(false);

  async function loadChannel() {
    setLoading(true);
    try {
      const result = await channelApi.getChannel(channelId, currentUserId);
      setChannel(result);
      setSelectedChannelId(channelId);
    } catch (error) {
      showNotice(error.message, 'error');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadChannel();
  }, [channelId, currentUserId]);

  async function handleSubscribe(nextSubscribed) {
    try {
      const result = nextSubscribed
        ? await channelApi.subscribe(channelId)
        : await channelApi.unsubscribe(channelId);
      setChannel((current) => ({
        ...current,
        subscribed: result.subscribed,
        subscriberCount: result.subscriberCount
      }));
      showNotice(nextSubscribed ? 'Subscribed' : 'Unsubscribed', 'success');
    } catch (error) {
      showNotice(error.status === 401 ? 'Please login before subscribing' : error.message, 'error');
    }
  }

  if (!channel) {
    return <div className="empty-state">{loading ? 'Loading channel...' : 'Channel not found.'}</div>;
  }

  return (
    <section className="page">
      <div className="channel-header">
        <div className="avatar">{channel.channelInfo?.username?.charAt(0)?.toUpperCase() || 'C'}</div>
        <div>
          <h1>{channel.channelInfo?.username || channel.channelInfo?.channelId}</h1>
          <p>{channel.subscriberCount} subscribers</p>
        </div>
        <div className="channel-actions">
          <button type="button" onClick={loadChannel} disabled={loading}>
            <RefreshCw size={16} />Refresh
          </button>
          {channel.subscribed ? (
            <button type="button" onClick={() => handleSubscribe(false)}>
              <BellOff size={16} />Unsubscribe
            </button>
          ) : (
            <button type="button" onClick={() => handleSubscribe(true)}>
              <Bell size={16} />Subscribe
            </button>
          )}
        </div>
      </div>
      <VideoGrid videos={channel.videos || []} emptyText="This channel has no public videos." />
    </section>
  );
}
